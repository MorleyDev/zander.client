package uk.co.morleydev.zander.client.test.spec.util

import java.io.{ByteArrayInputStream, File, FileNotFoundException, PrintWriter}

import com.github.kristofa.test.http.{Method, SimpleHttpResponseProvider}
import com.lambdaworks.jacks.JacksMapper
import org.apache.commons.io.FileUtils
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.mockito.{Matchers, Mockito}
import org.scalatest.mock.MockitoSugar
import uk.co.morleydev.zander.client.Main
import uk.co.morleydev.zander.client.data.{NativeProcessBuilder, NativeProcessBuilderFactory}
import uk.co.morleydev.zander.client.model.{Configuration, ProgramConfiguration}
import uk.co.morleydev.zander.client.test.gen.{GenNative, GenStringArguments}
import uk.co.morleydev.zander.client.test.spec.model.{CachedArtefactDetails, InstalledArtefactDetails}
import uk.co.morleydev.zander.client.test.spec.{SpecTest, TestConfigurationFile}
import uk.co.morleydev.zander.client.test.util.{CreateMockHttpServer, CreateMockProcess, MockServerAndPort, TemporaryDirectory}
import uk.co.morleydev.zander.client.util.using

import scala.collection.{JavaConversions, mutable}
import scala.io.Source

class RealTestHarness(parent : SpecTest) extends MockitoSugar with AutoCloseable {

  private val programs = new ProgramConfiguration(GenNative.genAlphaNumericString(3, 10),
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
  private var branch : String = "master"
  private val cache : TemporaryDirectory = new TemporaryDirectory()
  private val tmp : TemporaryDirectory = new TemporaryDirectory()
  private val working : TemporaryDirectory = new TemporaryDirectory(true)

  private var mockGitDownloadProcessBuilder : (NativeProcessBuilder, Process) = null
  private var mockGitCheckoutProcessBuilder : (NativeProcessBuilder, Process) = null
  private var mockGitVersionProcessBuilder : (NativeProcessBuilder, Process) = null
  private var mockCmakeProcessBuilder : (NativeProcessBuilder, Process) = null
  private var mockCmakeBuildProcessBuilder : (NativeProcessBuilder, Process) = null
  private var mockCmakeInstallProcessBuilder : (NativeProcessBuilder, Process) = null

  private var responseCode : Int = Int.MinValue

  private var installedFiles : Seq[String] = null
  private var installedArtefactDetails : InstalledArtefactDetails = null

  def givenAServer() : RealTestHarness = {
    provider = new SimpleHttpResponseProvider()
    mockServer = CreateMockHttpServer(provider)
    this
  }

  def givenFullGitPipelineIsPossible(expectedArtefactVersion : String) : RealTestHarness =
    givenAGitDownloadIsPossible()
      .givenAGitCheckoutIsPossible()
      .givenAGitVersionIsPossible(expectedArtefactVersion)

  def givenAGitDownloadIsPossible() : RealTestHarness = {
    mockGitDownloadProcessBuilder = CreateMockProcess()
    mockProcessBuilderQueue.enqueue(mockGitDownloadProcessBuilder._1)
    this
  }

  def givenAGitCheckoutIsPossible() : RealTestHarness = {
    mockGitCheckoutProcessBuilder = CreateMockProcess()
    mockProcessBuilderQueue.enqueue(mockGitCheckoutProcessBuilder._1)
    this
  }

  def givenAGitVersionIsPossible(expectedArtefactVersion : String) : RealTestHarness = {
    mockGitVersionProcessBuilder = CreateMockProcess(() => 0, new ByteArrayInputStream(expectedArtefactVersion.getBytes("UTF-8")))
    mockProcessBuilderQueue.enqueue(mockGitVersionProcessBuilder._1)
    this
  }

  def givenFullCMakePipelineIsPossible(expectedFiles : Seq[String]) : RealTestHarness =
    givenACMakePrebuildIsPossible()
      .givenACMakeBuildIsPossible()
      .givenACMakeInstallIsPossible(expectedFiles)

  def givenACMakePrebuildIsPossible() : RealTestHarness = {
    mockCmakeProcessBuilder = CreateMockProcess()
    mockProcessBuilderQueue.enqueue(mockCmakeProcessBuilder._1)
    this
  }

  def givenACMakeBuildIsPossible() : RealTestHarness = {
    mockCmakeBuildProcessBuilder = CreateMockProcess()
    mockProcessBuilderQueue.enqueue(mockCmakeBuildProcessBuilder._1)
    this
  }

  def givenACMakeInstallIsPossible(expectedFiles : Seq[String]) : RealTestHarness = {
    mockCmakeInstallProcessBuilder = CreateMockProcess(() => {
      expectedFiles.foreach(path => {
        val file = cache.sub(arguments(1) + "/" + branch + "/" + arguments(2) + "." + arguments(3) + "/" + path)
        if (!file.getParentFile.exists()) file.getParentFile.mkdirs()
        file.createNewFile()
      })
      0
    })
    mockProcessBuilderQueue.enqueue(mockCmakeInstallProcessBuilder._1)
    this
  }

  def whenRanWithArguments(args : Array[String]): RealTestHarness = {
    arguments = args
    this
  }

  def whenExecutingOperation(operation : String = GenStringArguments.genOperation(),
                             project : String = GenStringArguments.genProject(),
                             compiler : String = GenStringArguments.genCompiler(),
                             mode : String = GenStringArguments.genBuildMode(),
                              extraArgs : Array[String] = Array[String]()): RealTestHarness =
    whenRanWithArguments(Array[String](operation, project, compiler, mode) ++ extraArgs)

  def whenGetting(project : String = GenStringArguments.genProject(),
                  compiler : String = GenStringArguments.genCompiler(),
                  mode : String = GenStringArguments.genBuildMode()) : RealTestHarness = {
    branch = "master"
    whenExecutingOperation("get", project, compiler, mode)
  }

  def whenGettingBranch(project : String = GenStringArguments.genProject(),
                  compiler : String = GenStringArguments.genCompiler(),
                  mode : String = GenStringArguments.genBuildMode(),
                  branch : String = "master") : RealTestHarness = {
    this.branch = branch
    whenExecutingOperation("get", project, compiler, mode, Array[String]("--branch=%s".format(branch)))
  }

  def whenInstalling(project : String = GenStringArguments.genProject(),
                     compiler : String = GenStringArguments.genCompiler(),
                     mode : String = GenStringArguments.genBuildMode()) : RealTestHarness =
    whenExecutingOperation("install", project, compiler, mode)

  def whenPurging(project : String = GenStringArguments.genProject(),
                  compiler : String = GenStringArguments.genCompiler(),
                  mode : String = GenStringArguments.genBuildMode()) : RealTestHarness =
    whenExecutingOperation("purge", project, compiler, mode)

  def whenUpdating(project : String = GenStringArguments.genProject(),
                   compiler : String = GenStringArguments.genCompiler(),
                   mode : String = GenStringArguments.genBuildMode()) : RealTestHarness =
    whenExecutingOperation("update", project, compiler, mode)

  def whenTheArtefactsAreLocallyInstalled(version: String = GenNative.genAlphaNumericString(10, 100),
                                       expectedFiles: Seq[String] = Seq[String]()) : RealTestHarness = {
    using(new PrintWriter(working.sub("%s.%s.%s.json".format(arguments(1), arguments(2), arguments(3))))) {
      writer => JacksMapper.writeValue(writer,
        new InstalledArtefactDetails(version, expectedFiles))
    }
    expectedFiles.map(f => working.sub(f)).foreach(f => { f.getParentFile.mkdirs(); f.createNewFile() })
    this
  }

  def whenArtefactsAreLocallyInstalledForAnotherProject(version: String = GenNative.genAlphaNumericString(10, 100),
                                       expectedFiles: Seq[String] = Seq[String]()) : RealTestHarness = {
    val thisProject = "%s.%s.%s.json".format(arguments(1), arguments(2), arguments(3))
    val otherProject = Iterator.continually("%s.%s.%s.json".format(GenStringArguments.genProject(),
      GenStringArguments.genCompiler(),
      GenStringArguments.genBuildMode())).dropWhile(_ == thisProject).take(1).toList.head

    using(new PrintWriter(working.sub(otherProject))) {
      writer => JacksMapper.writeValue(writer,
        new InstalledArtefactDetails(version, expectedFiles))
    }
    expectedFiles.map(f => working.sub(f)).foreach(f => { f.getParentFile.mkdirs(); f.createNewFile() })
    this
  }

  def whenTheCacheAlreadyContainsTheSourceCode() : RealTestHarness = {
    new File(cache.file, arguments(1) + "/source").mkdirs()
    this
  }

  def whenTheCacheAlreadyContainsArtefacts(version : String, files : Seq[String]) : RealTestHarness = {

    val cachedArtefactStore = cache.sub("%s/%s/%s.%s".format(arguments(1), branch, arguments(2), arguments(3)))
    cachedArtefactStore.mkdirs()
    using(new PrintWriter(new File(cachedArtefactStore, "version.json"))) {
      writer => writer.write(JacksMapper.writeValueAsString[CachedArtefactDetails](new CachedArtefactDetails(version)))
    }

    files.foreach(path => {
      val file = new File(cachedArtefactStore, path)
      if (!file.getParentFile.exists()) file.getParentFile.mkdirs()
      file.createNewFile()
    })
    this
  }

  def expectRequestToThenReplyWith(endpointUrl : String, responseCode : Int, responseBody : String) : RealTestHarness = {
    provider.expect(Method.GET, endpointUrl)
      .respondWith(responseCode, "application/json", responseBody)
    this
  }

  def expectRequestToArgumentEndpointThenReplyWith(responseCode : Int, responseBody : String) : RealTestHarness =
    expectRequestToThenReplyWith("/project/%s".format(arguments(1)), responseCode, responseBody)

  def expectSuccessfulRequest(gitUrl : String) : RealTestHarness =
    expectRequestToThenReplyWith("/project/%s".format(arguments(1)), 200, "{ \"src\" : { \"vcs\" : \"git\", \"href\" : \"%s\" } }".format(gitUrl))

  def invokeMain() : RealTestHarness = {

    val host =
      if (mockServer == null)
        "http://localhost:" + 7999
    else
        "http://localhost:" + theServerPort()

    val configuration = new Configuration(host, programs, cache.file.getAbsolutePath)

    val thrownException = try {
      using(new TestConfigurationFile(configuration)) {
        config =>
          responseCode = Main.main(arguments,
            config.file.getPath,
            mockProcessBuilderFactory,
            tmp.file,
            working.file)
      }
      null
    } catch {
      case e : Throwable => e
    }

    installedFiles = {
      def seqOfFiles(dir: File) =
        if (dir.exists())
          JavaConversions.collectionAsScalaIterable(FileUtils.listFiles(dir, null, true))
            .asInstanceOf[Iterable[File]]
            .map(f => f.getAbsolutePath)
            .toSeq
        else
          Seq[String]()

      (seqOfFiles(working.sub("include"))
        ++ seqOfFiles(working.sub("lib"))
        ++ seqOfFiles(working.sub("bin"))).toList
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
      case e: FileNotFoundException => null
      case e: ArrayIndexOutOfBoundsException => null
    }

    parent.it("Then no exception escaped") {
      val desc = if (thrownException == null)
        ""
      else
        "Expected no exception to escape but " + thrownException.getStackTrace.mkString("\n")

      parent._assert(thrownException == null, desc)
    }
    this
  }

  def thenTheExpectedServerRequestsWereHandled() : RealTestHarness = {
    parent.it("Then the expected server requests were handled") {
      mockServer.server.verify()
    }
    this
  }

  def thenExpectedResponseCodeWasReturned(expected : Int) : RealTestHarness = {
    parent.it("Then the response code was as expected") {
      parent._assert(responseCode == expected, "Expected %s but was %s".format(expected, responseCode))
    }
    this
  }

  def thenAGitCloneWasInvoked(gitUrl : String) : RealTestHarness = {
    parent.it("Then a git clone was invoked on " + gitUrl) {
      Mockito.verify(mockProcessBuilderFactory).apply(Seq[String](programs.git, "clone", gitUrl, "source"))
      Mockito.verify(mockGitDownloadProcessBuilder._1).directory(cache.sub(arguments(1)))
      Mockito.verify(mockGitDownloadProcessBuilder._1).start()
      Mockito.verify(mockGitDownloadProcessBuilder._2).waitFor()
    }
    this
  }

  def thenAGitCheckoutWasInvoked() : RealTestHarness = {
    parent.it("Then a git clone was invoked on " + branch) {
      Mockito.verify(mockProcessBuilderFactory).apply(Seq[String](programs.git, "checkout", branch))
      Mockito.verify(mockGitCheckoutProcessBuilder._1).directory(cache.sub(arguments(1) + "/source"))
      Mockito.verify(mockGitCheckoutProcessBuilder._1).start()
      Mockito.verify(mockGitCheckoutProcessBuilder._2).waitFor()
    }
    this
  }

  def thenAGitUpdateWasInvoked() : RealTestHarness = {
    parent.it("Then a git update was invoked") {
      Mockito.verify(mockProcessBuilderFactory).apply(Seq[String](programs.git, "pull"))
      Mockito.verify(mockGitDownloadProcessBuilder._1).directory(cache.sub(arguments(1) + "/source"))
      Mockito.verify(mockGitDownloadProcessBuilder._1).start()
      Mockito.verify(mockGitDownloadProcessBuilder._2).waitFor()
    }
    this
  }

  def thenTheGitVersionWasRetrieved() : RealTestHarness = {
    parent.it("Then the git source version was retrieved") {
      Mockito.verify(mockProcessBuilderFactory).apply(Seq[String](programs.git, "rev-parse", "HEAD"))
      Mockito.verify(mockGitVersionProcessBuilder._1).directory(cache.sub(arguments(1) + "/source"))
      Mockito.verify(mockGitVersionProcessBuilder._1).start()
      Mockito.verify(mockGitVersionProcessBuilder._2).waitFor()
    }
    this
  }

  def thenACMakePreBuildWasInvoked(buildType : String, generator: String) : RealTestHarness = {

    val generatorSequence = Array("-G", generator)

    parent.it("Then a cmake prebuild was invoked") {
      Mockito.verify(mockProcessBuilderFactory).apply(Seq[String](programs.cmake,
        new File(cache.file, arguments(1) + "/source").getAbsolutePath) ++
        generatorSequence ++
        Seq[String]("-DCMAKE_BUILD_TYPE=" + buildType,
        "-DCMAKE_INSTALL_PREFIX=" + cache.sub(arguments(1) + "/" + branch + "/" + arguments(2) + "." + arguments(3)).getAbsolutePath
      ))

      Mockito.verify(mockCmakeProcessBuilder._1).directory(tmp.file)
      Mockito.verify(mockCmakeProcessBuilder._1).start()
      Mockito.verify(mockCmakeProcessBuilder._2).waitFor()
    }
    this
  }

  def thenACMakeBuildWasInvoked(buildType : String) : RealTestHarness = {
    parent.it("Then a cmake build was invoked") {
      Mockito.verify(mockProcessBuilderFactory).apply(Seq[String](programs.cmake, "--build", ".", "--config", buildType))
      Mockito.verify(mockCmakeBuildProcessBuilder._1).directory(tmp.file)
      Mockito.verify(mockCmakeBuildProcessBuilder._1).start()
      Mockito.verify(mockCmakeBuildProcessBuilder._2).waitFor()
    }
    this
  }

  def thenACMakeInstallWasInvoked(buildType: String) : RealTestHarness = {

    parent.it("Then a make install was invoked") {
      Mockito.verify(mockProcessBuilderFactory).apply(Seq[String](programs.cmake, "--build", ".", "--config", buildType, "--target", "install"))
      Mockito.verify(mockCmakeInstallProcessBuilder._1).directory(tmp.file)
      Mockito.verify(mockCmakeInstallProcessBuilder._1).start()
      Mockito.verify(mockCmakeInstallProcessBuilder._2).waitFor()
    }
    this
  }

  def thenTheExpectedFilesWereInstalledLocally(expectedFiles : Seq[String]) : RealTestHarness = {
    parent.it("Then the expected files were installed locally") {

      val expectedWorkingDirectoryFiles = expectedFiles.map(filename => working.sub(filename).getAbsolutePath)

      parent._assert(expectedWorkingDirectoryFiles.forall(s => installedFiles.contains(s)),
             "Expected files were not installed locally %s".format(expectedWorkingDirectoryFiles
               .filterNot(s => installedFiles.contains(s)).toSeq))
    }
    this
  }

  def thenTheExpectedFilesWereRemovedLocally(expectedFiles : Seq[String]) : RealTestHarness = {
    parent.it("Then the expected files were removed locally") {
      val expectedWorkingDirectoryFiles = expectedFiles.map(filename => working.sub(filename).getAbsolutePath)

      val workingDirIsNotOverlapWithInstalled = expectedWorkingDirectoryFiles.forall(s => !installedFiles.contains(s))
      parent._assert(workingDirIsNotOverlapWithInstalled,
        "Expected files were installed locally %s".format(expectedWorkingDirectoryFiles
          .filter(s => installedFiles.contains(s)).toSeq))
    }
    this
  }

  def thenTheLocalArtefactsWereNotTaggedWithDetails() : RealTestHarness = {
    parent.it("Then the installed files were not tagged") {
      parent._assert(installedArtefactDetails == null,
             "Expected %s.%s.%s.json to not exist but it existed".format(arguments(1), arguments(2), arguments(3)))
    }
    this
  }

  def thenTheLocalArtefactsWereTaggedWithTheExpectedVersion(expectedVersion : String) : RealTestHarness = {
    parent.it("Then the installed files were tagged with the expected version") {
      parent._assert(installedArtefactDetails.version == expectedVersion,
             "Expected %s but was %s".format(expectedVersion, installedArtefactDetails.version))
    }
    this
  }

  def thenTheLocalArtefactsWereTaggedWithTheExpectedFiles(expectedFiles : Seq[String]) : RealTestHarness = {
    parent.it("Then the installed files were tagged with the expected files") {
      parent._assert(installedArtefactDetails.files.diff(expectedFiles).size == 0,
        "Expected %s but was %s".format(expectedFiles, installedArtefactDetails.files))
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
