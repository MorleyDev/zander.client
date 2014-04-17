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
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock

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

  def testCase(mode : String) = {
    val arguments = Array[String]("install",
      GenStringArguments.genProject(),
      "gnu",
      mode)

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

        val mockProcessBuilderFactory = mock[NativeProcessBuilderFactory]
        Mockito.when(mockProcessBuilderFactory.apply(Matchers.any[Seq[String]]))
          .thenReturn(mockGitProcessBuilder._1)
          .thenReturn(mockCmakeProcessBuilder._1)
          .thenReturn(mockCmakeBuildProcessBuilder._1)
          .thenReturn(mockCmakeInstallProcessBuilder._1)

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

        val cmakeSourceTmpPath = new File(configuration.cache, "tmp")
        it("Then the cmake process was invoked") {
          val cmakeSourcePath = new File(configuration.cache + "/" + arguments(1) + "/source")

          Mockito.verify(mockProcessBuilderFactory).apply(Seq[String](programs.cmake,
            cmakeSourcePath.getAbsolutePath,
            "-G\"MinGW Makefiles\"",
            "-DCMAKE_BUILD_TYPE=" + (if (arguments(3) == "debug") "Debug" else "Release"),
            "-DCMAKE_INSTALL_PREFIX=" + new File(configuration.cache, arguments(1) + "/" + arguments(2) + "." + mode).getAbsolutePath
          ))

          Mockito.verify(mockCmakeProcessBuilder._1).directory(cmakeSourceTmpPath)
          Mockito.verify(mockCmakeProcessBuilder._1).start()
          Mockito.verify(mockCmakeProcessBuilder._2).waitFor()
        }
        it("Then the cmake build process was invoked") {
          Mockito.verify(mockProcessBuilderFactory).apply(Seq[String](programs.cmake, "--build", "."))
          Mockito.verify(mockCmakeBuildProcessBuilder._1).directory(cmakeSourceTmpPath)
          Mockito.verify(mockCmakeBuildProcessBuilder._1).start()
          Mockito.verify(mockCmakeBuildProcessBuilder._2).waitFor()
        }
        it("Then the cmake install process was invoked") {
          Mockito.verify(mockProcessBuilderFactory).apply(Seq[String](programs.cmake, "--build", ".", "--", "install"))
          Mockito.verify(mockCmakeInstallProcessBuilder._1).directory(cmakeSourceTmpPath)
          Mockito.verify(mockCmakeInstallProcessBuilder._1).start()
          Mockito.verify(mockCmakeInstallProcessBuilder._2).waitFor()
        }
        it("Then the expected return code is returned") {
          assert(responseCode == ResponseCodes.Success)
        }
      }
    }
  }
  testCase("debug")
  testCase("release")
}
