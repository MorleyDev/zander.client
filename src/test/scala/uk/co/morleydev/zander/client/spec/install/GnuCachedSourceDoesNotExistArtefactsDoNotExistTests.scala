package uk.co.morleydev.zander.client.spec.install

import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfter, FunSpec}
import com.github.kristofa.test.http.{Method, SimpleHttpResponseProvider}
import uk.co.morleydev.zander.client.util.{CreateMockProcess, CreateMockHttpServer}
import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.gen.{GenStringArguments, GenNative}
import uk.co.morleydev.zander.client.util.Using._
import uk.co.morleydev.zander.client.model.{Configuration, ProgramConfiguration}
import uk.co.morleydev.zander.client.spec.{ResponseCodes, TestConfigurationFile}
import uk.co.morleydev.zander.client.Main
import java.io.{ByteArrayInputStream, File}
import uk.co.morleydev.zander.client.data.NativeProcessBuilderFactory
import org.apache.commons.io.FileUtils
import scala.collection.JavaConversions

class GnuCachedSourceDoesNotExistArtefactsDoNotExistTests extends FunSpec with MockitoSugar with BeforeAndAfter {

  def testCase(mode: String, cmakeBuildType: String) = {
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
        val targetIncludeDir = new File("include")
        val targetLibDir = new File("lib")
        val targetBinDir = new File("bin")

        val expectedFiles = Seq[String]("include/" + GenNative.genAlphaNumericString(1, 20),
          "include/sub_dir/" + GenNative.genAlphaNumericString(1, 20) + ".h",
          "lib/" + GenNative.genAlphaNumericString(1, 20) + ".a",
          "lib/subdir/" + GenNative.genAlphaNumericString(1, 20) + ".a",
          "lib/" + GenNative.genAlphaNumericString(1, 20) + ".dll",
          "lib/" + GenNative.genAlphaNumericString(1, 20) + ".so",
          "lib/" + GenNative.genAlphaNumericString(1, 20) + ".so.12.2",
          "lib/subdir2/" + GenNative.genAlphaNumericString(1, 20) + ".so",
          "lib/subdir2/" + GenNative.genAlphaNumericString(1, 20) + ".so.12.32",
          "lib/subdir2/" + GenNative.genAlphaNumericString(1, 20) + ".dll",
          "bin/" + GenNative.genAlphaNumericString(1, 20) + ".dll",
          "bin/" + GenNative.genAlphaNumericString(1, 20) + ".so",
          "bin/" + GenNative.genAlphaNumericString(1, 20) + ".so.12.25.a",
          "bin/subdir/" + GenNative.genAlphaNumericString(1, 20) + ".dll",
          "bin/subdir2/" + GenNative.genAlphaNumericString(1, 20) + ".so",
          "bin/subdir/" + GenNative.genAlphaNumericString(1, 20) + ".so.12.25.a")

        val programs = new ProgramConfiguration(GenNative.genAlphaNumericString(3, 10),
          GenNative.genAlphaNumericString(3, 10),
          GenNative.genAlphaNumericString(3, 10))
        val configuration = new Configuration("http://localhost:" + mockHttpServer.port, programs, cache = "cache")
        val cacheDirectory = new File(configuration.cache)
        val temporaryDirectory = new File("tmp" + GenNative.genAlphaNumericString(1, 20))

        val mockGitProcessBuilder = CreateMockProcess()
        val mockCmakeProcessBuilder = CreateMockProcess()
        val mockCmakeBuildProcessBuilder = CreateMockProcess()
        val mockCmakeInstallProcessBuilder = CreateMockProcess(() => {
          println("CMake Install Invoked")
          expectedFiles.foreach(path => {
            val file = new File(cacheDirectory, arguments(1) + "/" + arguments(2) + "." + arguments(3) + "/" + path)
            if (!file.getParentFile.exists()) file.getParentFile.mkdirs()
            if (file.createNewFile())
              println("Created file " + file)
            else println("Failed to create " + file)
          })
          0
        })

        val mockProcessBuilderFactory = mock[NativeProcessBuilderFactory]
        Mockito.when(mockProcessBuilderFactory.apply(Matchers.any[Seq[String]]))
          .thenReturn(mockGitProcessBuilder._1)
          .thenReturn(mockCmakeProcessBuilder._1)
          .thenReturn(mockCmakeBuildProcessBuilder._1)
          .thenReturn(mockCmakeInstallProcessBuilder._1)

        var responseCode = -1

        using(new TestConfigurationFile(configuration)) {
          config =>
            responseCode = Main.main(arguments, config.file.getPath, mockProcessBuilderFactory, temporaryDirectory)
        }

        val installedFiles = {
          def seqOfFiles(dir: File) =
            JavaConversions.asScalaIterator(FileUtils.iterateFiles(dir, null, true)).toSeq
          seqOfFiles(targetIncludeDir) ++ seqOfFiles(targetLibDir) ++ seqOfFiles(targetBinDir)
        }

        FileUtils.deleteDirectory(temporaryDirectory)
        FileUtils.deleteDirectory(cacheDirectory)
        FileUtils.deleteDirectory(targetIncludeDir)
        FileUtils.deleteDirectory(targetLibDir)
        FileUtils.deleteDirectory(targetBinDir)

        it("Then the endpoint was requested") {
          provider.verify()
        }
        it("Then the git process was invoked") {
          Mockito.verify(mockProcessBuilderFactory).apply(Seq[String](programs.git, "clone", gitUrl, "source"))
          Mockito.verify(mockGitProcessBuilder._1).directory(new File(cacheDirectory, arguments(1)))
          Mockito.verify(mockGitProcessBuilder._1).start()
          Mockito.verify(mockGitProcessBuilder._2).waitFor()
        }

        val cmakeSourceTmpPath = temporaryDirectory
        it("Then the cmake process was invoked") {
          val cmakeSourcePath = new File(cacheDirectory, arguments(1) + "/source")

          Mockito.verify(mockProcessBuilderFactory).apply(Seq[String](programs.cmake,
            cmakeSourcePath.getAbsolutePath,
            "-G\"MinGW", "Makefiles\"",
            "-DCMAKE_BUILD_TYPE=" + cmakeBuildType,
            "-DCMAKE_INSTALL_PREFIX=" + new File(cacheDirectory, arguments(1) + "/" + arguments(2) + "." + mode).getAbsolutePath
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
        it("Then the files were installed locally") {
          assert(expectedFiles.map(filename => new File(filename)).forall(f => installedFiles.contains(f)))
        }
      }
    }
  }
  testCase("debug", "Debug")
  testCase("release", "Release")
}
