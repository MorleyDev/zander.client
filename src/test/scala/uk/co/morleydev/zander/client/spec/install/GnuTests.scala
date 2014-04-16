package uk.co.morleydev.zander.client.spec.install

import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSpec
import com.github.kristofa.test.http.{Method, SimpleHttpResponseProvider}
import uk.co.morleydev.zander.client.util.CreateMockHttpServer
import scala.collection.mutable
import org.mockito.{ArgumentMatcher, Matchers, Mockito}
import uk.co.morleydev.zander.client.gen.{GenStringArguments, GenNative}
import uk.co.morleydev.zander.client.util.Using._
import uk.co.morleydev.zander.client.model.{Configuration, ProgramConfiguration}
import uk.co.morleydev.zander.client.spec.{ResponseCodes, TestConfigurationFile}
import uk.co.morleydev.zander.client.Main
import java.io.File
import uk.co.morleydev.zander.client.data.{NativeProcessBuilderFactory, NativeProcessBuilder}

class GnuTests extends FunSpec with MockitoSugar {

  def createMockProcess(): (NativeProcessBuilder, Process) = {

    val mockProcess = mock[Process]
    Mockito.when(mockProcess.exitValue())
      .thenReturn(0)
    Mockito.when(mockProcess.getInputStream)
      .thenReturn(GenNative.genInputStreamString())
    Mockito.when(mockProcess.getErrorStream)
      .thenReturn(GenNative.genInputStreamString())
    Mockito.when(mockProcess.waitFor())
      .thenReturn(0)

    val mockProcessBuilder = mock[NativeProcessBuilder]
    Mockito.when(mockProcessBuilder.directory(Matchers.any[File]()))
      .thenReturn(mockProcessBuilder)
    Mockito.when(mockProcessBuilder.start())
      .thenReturn(mockProcess)

    (mockProcessBuilder, mockProcess)
  }

  class StringPathIsFilePath(val expectedPath: File) extends ArgumentMatcher[String] {
    override def matches(argument: scala.Any): Boolean = {
      new File(argument.asInstanceOf[String]).getAbsolutePath == expectedPath.getAbsolutePath
    }
  }

  private val arguments = Array[String]("install",
    GenStringArguments.genProject(),
    "gnu",
    GenStringArguments.genBuildMode())
  describe("Given the project/compiler endpoint exists") {

    val endpointUrl = "/" + arguments(1) + "/" + arguments(2)
    val gitUrl = "http://git_url/request/at_me"
    val responseBody = "{ \"git\":\"" + gitUrl + "\" }"

    val provider = new SimpleHttpResponseProvider()
    val mockHttpServer = CreateMockHttpServer(provider)
    mockHttpServer.server.start()

    provider.expect(Method.GET, endpointUrl)
      .respondWith(200, "application/json", responseBody)

    describe("When an install operation is carried out with arguments " + arguments.mkString(", ")) {

      val mockGitProcessBuilder = createMockProcess()
      val mockCmakeProcessBuilder = createMockProcess()
      val mockCmakeBuildProcessBuilder = createMockProcess()
      val mockCmakeInstallProcessBuilder = createMockProcess()

      val processQueue = new mutable.Queue[NativeProcessBuilder]
      processQueue.enqueue(mockGitProcessBuilder._1)
      processQueue.enqueue(mockCmakeProcessBuilder._1)
      processQueue.enqueue(mockCmakeBuildProcessBuilder._1)
      processQueue.enqueue(mockCmakeInstallProcessBuilder._1)

      val mockProcessBuilderFactory = mock[NativeProcessBuilderFactory]
      Mockito.when(mockProcessBuilderFactory.apply(Matchers.any[Seq[String]]))
        .thenReturn(processQueue.dequeue())

      var responseCode = -1
      val programs = new ProgramConfiguration(GenNative.genAlphaNumericString(3, 10),
        GenNative.genAlphaNumericString(3, 10),
        GenNative.genAlphaNumericString(3, 10))

      val configuration = new Configuration("http://localhost:" + mockHttpServer.port, programs, cache = "./cache/directory/")
      using(new TestConfigurationFile(configuration)) {
        config =>
          Main.main(arguments, config.file.getPath, s => responseCode = s, mockProcessBuilderFactory)

      }
      it("Then the endpoint was requested") {
        provider.verify()
      }
      it("Then the git process was invoked") {
        Mockito.verify(mockProcessBuilderFactory).apply(Seq[String](programs.git, "clone", gitUrl, "source"))
        Mockito.verify(mockGitProcessBuilder._1).directory(new File(configuration.cache, arguments(1)))
        Mockito.verify(mockGitProcessBuilder._1).start()
        Mockito.verify(mockGitProcessBuilder._2).waitFor()
      }

      it("Then the expected return code is returned") {
        assert(responseCode == ResponseCodes.Success)
      }
    }
  }
}
