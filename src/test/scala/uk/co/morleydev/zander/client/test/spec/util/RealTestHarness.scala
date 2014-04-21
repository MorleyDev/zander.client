package uk.co.morleydev.zander.client.test.spec.util

import com.github.kristofa.test.http.{Method, SimpleHttpResponseProvider}
import com.lambdaworks.jacks.JacksMapper
import java.io.{PrintWriter, ByteArrayInputStream, File}
import org.apache.commons.io.FileUtils
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.mockito.{Matchers, Mockito}
import org.scalatest.mock.MockitoSugar
import scala.collection.{JavaConversions, mutable}
import scala.io.Source
import uk.co.morleydev.zander.client.Main
import uk.co.morleydev.zander.client.data.{NativeProcessBuilder, NativeProcessBuilderFactory}
import uk.co.morleydev.zander.client.model.Configuration
import uk.co.morleydev.zander.client.model.ProgramConfiguration
import uk.co.morleydev.zander.client.test.gen.{GenStringArguments, GenNative}
import uk.co.morleydev.zander.client.test.spec.TestConfigurationFile
import uk.co.morleydev.zander.client.test.spec.model.{CachedArtefactDetails, InstalledArtefactDetails}
import uk.co.morleydev.zander.client.test.util.{TemporaryDirectory, CreateMockProcess, CreateMockHttpServer, MockServerAndPort}
import uk.co.morleydev.zander.client.util.Using._

class RealTestHarness(parent : TestHarnessSpec) extends MockitoSugar with AutoCloseable {

  private val programs = new ProgramConfiguration(GenNative.genAlphaNumericString(3, 10),
    GenNative.genAlphaNumericString(3, 10),
    GenNative.genAlphaNumericString(3, 10))

  private val mockProcessBuilderFactory = mock[NativeProcessBuilderFactory]
  private val mockProcessBuilderQueue = new mutable.Queue[NativeProcessBuilder]

  Mockito.when(mockProcessBuilderFactory.apply(Matchers.any[Seq[String]]))
    .thenAnswer(new Answer[NativeProcessBuilder] {
    override def answer(invocation: InvocationOnMock) : NativeProcessBuilder = mockProcessBuilderQueue.dequeue()
  })

  private var provider : SimpleHttpResponseProvider = null
  private var mockServer : MockServerAndPort = null
  private var arguments : Array[String] = null
  private val cache : TemporaryDirectory = new TemporaryDirectory()
  private val tmp : TemporaryDirectory = new TemporaryDirectory()
  private val working : TemporaryDirectory = new TemporaryDirectory(true)

  private var mockGitProcessBuilder : (NativeProcessBuilder, Process) = null
  private var mockGitVersionProcessBuilder : (NativeProcessBuilder, Process) = null
  private var mockCmakeProcessBuilder : (NativeProcessBuilder, Process) = null
  private var mockCmakeBuildProcessBuilder : (NativeProcessBuilder, Process) = null
  private var mockCmakeInstallProcessBuilder : (NativeProcessBuilder, Process) = null

  private var responseCode : Int = Int.MinValue

  private var installedFiles : Seq[File] = null
  private var installedArtefactDetails : InstalledArtefactDetails = null

  def givenAServer() : RealTestHarness = {
    provider = new SimpleHttpResponseProvider()
    mockServer = CreateMockHttpServer(provider)
    mockServer.server.start()
    this
  }

  def givenGitIsPossible(expectedArtefactVersion : String) : RealTestHarness = {
    mockGitProcessBuilder = CreateMockProcess()
    mockGitVersionProcessBuilder = CreateMockProcess(
      () => 0,
      new ByteArrayInputStream(expectedArtefactVersion.getBytes("UTF-8")))

    mockProcessBuilderQueue.enqueue(mockGitProcessBuilder._1)
    mockProcessBuilderQueue.enqueue(mockGitVersionProcessBuilder._1)

    this
  }

  def givenFullCMakeBuildIsPossible(expectedFiles : Seq[String]) : RealTestHarness = {
    mockCmakeProcessBuilder = CreateMockProcess()
    mockCmakeBuildProcessBuilder = CreateMockProcess()
    mockCmakeInstallProcessBuilder = CreateMockProcess(() => {
      println("CMake Install Invoked")
      expectedFiles.foreach(path => {
        val file = cache.sub(arguments(1) + "/" + arguments(2) + "." + arguments(3) + "/" + path)
        if (!file.getParentFile.exists()) file.getParentFile.mkdirs()
        if (file.createNewFile())
          println("Created file " + file)
        else println("Failed to create " + file)
      })
      0
    })

    mockProcessBuilderQueue.enqueue(mockCmakeProcessBuilder._1)
    mockProcessBuilderQueue.enqueue(mockCmakeBuildProcessBuilder._1)
    mockProcessBuilderQueue.enqueue(mockCmakeInstallProcessBuilder._1)

    this
  }

  def whenInstalling(project : String = GenStringArguments.genProject(),
                     compiler : String = GenStringArguments.genCompiler(),
                     mode : String = GenStringArguments.genBuildMode()) : RealTestHarness = {
    arguments = Array[String]("install", project, compiler, mode)
    this
  }

  def whenArtefactsAreLocallyInstalled() : RealTestHarness = {
    using(new PrintWriter(working.sub("%s.%s.%s.json".format(arguments(1), arguments(2), arguments(3))))) {
      writer => JacksMapper.writeValue(writer, new InstalledArtefactDetails(GenNative.genAlphaNumericString(10, 100)))
    }
    this
  }

  def whenTheCacheAlreadyContainsTheSourceCode() : RealTestHarness = {
    new File(cache.file, arguments(1) + "/source").mkdirs()
    this
  }

  def whenTheCacheAlreadyContainsArtefacts(version : String, files : Seq[String]) : RealTestHarness = {

    val cachedArtefactStore = cache.sub("%s/%s.%s".format(arguments(1), arguments(2), arguments(3)))
    cachedArtefactStore.mkdirs()
    using(new PrintWriter(new File(cachedArtefactStore, "version.json"))) {
      writer => writer.write(JacksMapper.writeValueAsString[CachedArtefactDetails](new CachedArtefactDetails(version)))
    }

    files.foreach(path => {
      val file = new File(cachedArtefactStore, path)
      if (!file.getParentFile.exists()) file.getParentFile.mkdirs()
      if (file.createNewFile())
        println("Created file " + file)
      else println("Failed to create " + file)
    })
    this
  }

  def expectRequestToThenReplyWith(endpointUrl : String, responseCode : Int, responseBody : String) : RealTestHarness = {
    provider.expect(Method.GET, endpointUrl)
      .respondWith(responseCode, "application/json", responseBody)
    this
  }

  def expectRequestToArgumentEndpointThenReplyWith(responseCode : Int, responseBody : String) : RealTestHarness =
    expectRequestToThenReplyWith("/%s/%s".format(arguments(1), arguments(2)), responseCode, responseBody)

  def expectSuccessfulRequest(gitUrl : String) : RealTestHarness =
    expectRequestToThenReplyWith("/%s/%s".format(arguments(1), arguments(2)), 200, "{ \"git\":\"%s\" }".format(gitUrl))

  def invokeMain() : RealTestHarness = {

    val host =
      if (mockServer == null)
        "http://localhost:" + 7999
    else
        "http://localhost:" + theServerPort()

    val configuration = new Configuration(host, programs, cache.file.getAbsolutePath)

    using(new TestConfigurationFile(configuration)) {
      config =>
        responseCode = Main.main(arguments,
          config.file.getPath,
          mockProcessBuilderFactory,
          tmp.file,
          working.file)
    }

    installedFiles = {
      def seqOfFiles(dir: File) =
        if (dir.exists())
          JavaConversions.asScalaIterator(FileUtils.iterateFiles(dir, null, true))
            .asInstanceOf[Iterator[File]]
            .toSeq
        else
          Seq[File]()

      (seqOfFiles(working.sub("include"))
        ++ seqOfFiles(working.sub("lib"))
        ++ seqOfFiles(working.sub("bin")))
    }

    installedArtefactDetails = try {
      JacksMapper.readValue[InstalledArtefactDetails](
        using(Source.fromFile(
          working.sub("%s.%s.%s.json".format(arguments(1), arguments(2), arguments(3))))) {
          source =>
            source.getLines().mkString("\n")
        }
      )
    } catch {
      case e: Throwable =>
        println(e)
        new InstalledArtefactDetails("")
    }
    this
  }

  def thenTheExpectedServerRequestsWereHandled() : RealTestHarness = {

    parent._it("Then the expected server requests were handled") {
      mockServer.server.verify()
    }
    this
  }

  def thenTheResponseCodeWas(expected : Int) : RealTestHarness = {
    parent._it("Then the response code was as expected") {
      assert(responseCode == expected)
    }
    this
  }

  def thenAGitCloneWasInvoked(gitUrl : String) : RealTestHarness = {
    parent._it("Then a git clone was invoked on " + gitUrl) {
      Mockito.verify(mockProcessBuilderFactory).apply(Seq[String](programs.git, "clone", gitUrl, "source"))
      Mockito.verify(mockGitProcessBuilder._1).directory(cache.sub(arguments(1)))
      Mockito.verify(mockGitProcessBuilder._1).start()
      Mockito.verify(mockGitProcessBuilder._2).waitFor()
    }
    this
  }

  def thenAGitUpdateWasInvoked() : RealTestHarness = {
    parent._it("Then a git update was invoked") {
      Mockito.verify(mockProcessBuilderFactory).apply(Seq[String](programs.git, "pull"))
      Mockito.verify(mockGitProcessBuilder._1).directory(cache.sub(arguments(1) + "/source"))
      Mockito.verify(mockGitProcessBuilder._1).start()
      Mockito.verify(mockGitProcessBuilder._2).waitFor()
    }
    this
  }

  def thenTheGitVersionWasRetrieved() : RealTestHarness = {
    parent._it("Then the git source version was retrieved") {
      Mockito.verify(mockProcessBuilderFactory).apply(Seq[String](programs.git, "rev-parse", "HEAD"))
      Mockito.verify(mockGitVersionProcessBuilder._1).directory(cache.sub(arguments(1) + "/source"))
      Mockito.verify(mockGitVersionProcessBuilder._1).start()
      Mockito.verify(mockGitVersionProcessBuilder._2).waitFor()
    }
    this
  }

  def thenACMakePreBuildWasInvoked(buildType : String, generator: String) : RealTestHarness = {

    val generatorSequence = generator.split(' ')

    parent._it("Then a cmake prebuild was invoked") {
      Mockito.verify(mockProcessBuilderFactory).apply(Seq[String](programs.cmake,
        new File(cache.file, arguments(1) + "/source").getAbsolutePath) ++
        generatorSequence ++
        Seq[String]("-DCMAKE_BUILD_TYPE=" + buildType,
        "-DCMAKE_INSTALL_PREFIX=" + cache.sub(arguments(1) + "/" + arguments(2) + "." + arguments(3)).getAbsolutePath
      ))

      Mockito.verify(mockCmakeProcessBuilder._1).directory(tmp.file)
      Mockito.verify(mockCmakeProcessBuilder._1).start()
      Mockito.verify(mockCmakeProcessBuilder._2).waitFor()
    }
    this
  }

  def thenACMakeBuildWasInvoked(buildType : String) : RealTestHarness = {
    parent._it("Then a cmake build was invoked") {
      Mockito.verify(mockProcessBuilderFactory).apply(Seq[String](programs.cmake, "--build", ".", "--config", buildType))
      Mockito.verify(mockCmakeBuildProcessBuilder._1).directory(tmp.file)
      Mockito.verify(mockCmakeBuildProcessBuilder._1).start()
      Mockito.verify(mockCmakeBuildProcessBuilder._2).waitFor()
    }
    this
  }

  def thenAMakeInstallWasInvoked(buildType: String) : RealTestHarness = {

    parent._it("Then a make install was invoked") {
      Mockito.verify(mockProcessBuilderFactory).apply(Seq[String](programs.cmake, "--build", ".", "--config", buildType, "--target", "install"))
      Mockito.verify(mockCmakeInstallProcessBuilder._1).directory(tmp.file)
      Mockito.verify(mockCmakeInstallProcessBuilder._1).start()
      Mockito.verify(mockCmakeInstallProcessBuilder._2).waitFor()
    }
    this
  }

  def thenTheExpectedFilesWereInstalledLocally(expectedFiles : Seq[String]) : RealTestHarness = {
    parent._it("Then the expected files were installed locally") {
      val expectedWorkingDirectoryFiles = expectedFiles.map(filename => working.sub(filename))
      assert(installedFiles.diff(expectedWorkingDirectoryFiles).size == 0)
    }
    this
  }

  def thenTheLocalArtefactsWereTaggedWithTheExpectedVersion(version : String) : RealTestHarness = {
    parent._it("Then the installed files were tagged with the expected version") {
      assert(installedArtefactDetails.version == version)
    }
    this
  }

  def theServerPort() : Int = mockServer.port

  override def close(): Unit = {
    if (mockServer != null)
      mockServer.close()
    if (cache != null)
      cache.close()
    if (tmp != null)
      tmp.close()
    if (working != null)
      working.close()
  }
}
