package uk.co.morleydev.zander.client.spec.install

import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSpec
import com.github.kristofa.test.http.{Method, SimpleHttpResponseProvider}
import uk.co.morleydev.zander.client.util.CreateMockHttpServer
import scala.collection.mutable
import uk.co.morleydev.zander.client.data.program.{NativeProcessBuilderFactory, NativeProcessBuilder}
import org.mockito.{ArgumentMatcher, Matchers, Mockito}
import uk.co.morleydev.zander.client.gen.{GenStringArguments, GenNative}
import uk.co.morleydev.zander.client.util.Using._
import uk.co.morleydev.zander.client.model.{Configuration, ProgramConfiguration}
import uk.co.morleydev.zander.client.spec.{ResponseCodes, TestConfigurationFile}
import uk.co.morleydev.zander.client.Main
import java.io.File
import akka.util.ByteStringBuilder

class GnuTests extends FunSpec with MockitoSugar {

  def createMockProcess(): NativeProcessBuilder = {

    val mockProcess = mock[Process]
    Mockito.when(mockProcess.exitValue())
      .thenReturn(0)
    Mockito.when(mockProcess.getOutputStream)
      .thenReturn(new ByteStringBuilder().asOutputStream)
    Mockito.when(mockProcess.waitFor())
      .thenReturn(0)

    val mockProcessBuilder = mock[NativeProcessBuilder]
    Mockito.when(mockProcessBuilder.directory(Matchers.any[String]()))
      .thenReturn(mockProcessBuilder)
    Mockito.when(mockProcessBuilder.start())
      .thenReturn(mockProcess)

    mockProcessBuilder
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
      processQueue.enqueue(mockGitProcessBuilder)
      processQueue.enqueue(mockCmakeProcessBuilder)
      processQueue.enqueue(mockCmakeBuildProcessBuilder)
      processQueue.enqueue(mockCmakeInstallProcessBuilder)

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
          Main.main(arguments, config.file.getAbsolutePath, s => responseCode = s, mockProcessBuilderFactory)

      }

      it("Then the endpoint was requested") {
        provider.verify()
      }
      it("Then the git process was invoked") {
        Mockito.verify(mockProcessBuilderFactory).apply(Seq[String](programs.git, "clone", gitUrl, "source"))
        val programCacheDirectory = configuration.cache + "/" + arguments(1)
        Mockito.verify(mockGitProcessBuilder).directory(programCacheDirectory)
        Mockito.verify(mockGitProcessBuilder).start()
      }

      it("Then the expected return code is returned") {
        assert(responseCode == ResponseCodes.Success)
      }
    }
  }
}
