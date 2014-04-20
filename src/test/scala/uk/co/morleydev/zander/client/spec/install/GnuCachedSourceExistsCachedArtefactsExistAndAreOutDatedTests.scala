package uk.co.morleydev.zander.client.spec.install

import org.scalatest.FunSpec
import org.scalatest.mock.MockitoSugar
import com.github.kristofa.test.http.{Method, SimpleHttpResponseProvider}
import uk.co.morleydev.zander.client.util.Using._
import uk.co.morleydev.zander.client.util.{CreateMockProcess, TemporaryDirectory, CreateMockHttpServer}
import uk.co.morleydev.zander.client.model.{Configuration, ProgramConfiguration}
import uk.co.morleydev.zander.client.gen.{GenStringArguments, GenNative}
import java.io.{PrintWriter, ByteArrayInputStream, File}
import uk.co.morleydev.zander.client.data.NativeProcessBuilderFactory
import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.spec.{ResponseCodes, TestConfigurationFile}
import uk.co.morleydev.zander.client.Main
import scala.collection.JavaConversions
import org.apache.commons.io.FileUtils
import com.lambdaworks.jacks.JacksMapper
import uk.co.morleydev.zander.client.spec.model.{CachedArtefactDetails, InstalledArtefactDetails}
import scala.io.Source

class GnuCachedSourceExistsCachedArtefactsExistAndAreOutDatedTests extends FunSpec with MockitoSugar {

  def testCase(mode: String, cmakeBuildType: String) = {
    val arguments = Array[String]("install",
      GenStringArguments.genProject(),
      "gnu",
      mode)

    describe("Given a cached source and artefacts both exist") {

      val endpointUrl = "/" + arguments(1) + "/" + arguments(2)
      val gitUrl = "http://git_url/request/at_me"
      val responseBody = "{ \"git\":\"" + gitUrl + "\" }"

      val provider = new SimpleHttpResponseProvider()
      using(CreateMockHttpServer(provider)) {
        mockHttpServer =>
          mockHttpServer.server.start()

          provider.expect(Method.GET, endpointUrl)
            .respondWith(200, "application/json", responseBody)

          describe("When an install operation is carried out with arguments " + arguments.mkString(", ") + " and the source already exists but is out of date") {

            val programs = new ProgramConfiguration(GenNative.genAlphaNumericString(3, 10),
              GenNative.genAlphaNumericString(3, 10),
              GenNative.genAlphaNumericString(3, 10))

            using(new TemporaryDirectory(true),
              new TemporaryDirectory(),
              new TemporaryDirectory()) {
              (workingDirectory, cacheDirectory, temporaryDirectory) =>
                val configuration = new Configuration("http://localhost:" + mockHttpServer.port, programs, cache = cacheDirectory.file.getAbsolutePath)

                val targetIncludeDir = new File(workingDirectory.file, "include")
                val targetLibDir = new File(workingDirectory.file, "lib")
                val targetBinDir = new File(workingDirectory.file, "bin")

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

                cacheDirectory.sub(arguments(1) + "/source").mkdirs()

                val mockGitProcessBuilder = CreateMockProcess()

                val mockCmakeProcessBuilder = CreateMockProcess()
                val mockCmakeBuildProcessBuilder = CreateMockProcess()
                val mockCmakeInstallProcessBuilder = CreateMockProcess(() => {
                  println("CMake Install Invoked")
                  expectedFiles.foreach(path => {
                    val file = cacheDirectory.sub(arguments(1) + "/" + arguments(2) + "." + arguments(3) + "/" + path)
                    if (!file.getParentFile.exists()) file.getParentFile.mkdirs()
                    if (file.createNewFile())
                      println("Created file " + file)
                    else println("Failed to create " + file)
                  })
                  0
                })


                val oldArtefactVersion = GenNative.genAlphaNumericString(3, 100)
                val cachedArtefactStore = cacheDirectory.sub("%s/%s.%s".format(arguments(1), arguments(2), arguments(3)))
                cachedArtefactStore.mkdirs()
                using(new PrintWriter(new File(cachedArtefactStore, "version.json"))) {
                  writer =>
                    writer.write(JacksMapper.writeValueAsString[CachedArtefactDetails](new CachedArtefactDetails(oldArtefactVersion)))
                }

                val expectedNotFoundFiles = Seq[String]("include/IshouldNotExist.h")
                (expectedNotFoundFiles ++ expectedFiles).foreach(path => {
                  val file = new File(cachedArtefactStore, path)
                  if (!file.getParentFile.exists()) file.getParentFile.mkdirs()
                  if (file.createNewFile())
                    println("Created file " + file)
                  else println("Failed to create " + file)
                })

                val updatedArtefactVersion = GenNative.genAlphaNumericStringExcluding(3, 100, Seq[String](oldArtefactVersion))
                val mockGitVersionProcessBuilder = CreateMockProcess(() => 0,
                  new ByteArrayInputStream(updatedArtefactVersion.getBytes("UTF-8")))

                val mockProcessBuilderFactory = mock[NativeProcessBuilderFactory]
                Mockito.when(mockProcessBuilderFactory.apply(Matchers.any[Seq[String]]))
                  .thenReturn(mockGitProcessBuilder._1)
                  .thenReturn(mockGitVersionProcessBuilder._1)
                  .thenReturn(mockCmakeProcessBuilder._1)
                  .thenReturn(mockCmakeBuildProcessBuilder._1)
                  .thenReturn(mockCmakeInstallProcessBuilder._1)

                var responseCode = -1

                using(new TestConfigurationFile(configuration)) {
                  config =>
                    responseCode = Main.main(arguments,
                      config.file.getPath,
                      mockProcessBuilderFactory,
                      temporaryDirectory.file,
                      workingDirectory.file)
                }

                val installedFiles = {
                  def seqOfFiles(dir: File) =
                    JavaConversions.asScalaIterator(FileUtils.iterateFiles(dir, null, true))
                      .asInstanceOf[Iterator[File]]
                      .toSeq
                  (seqOfFiles(targetIncludeDir) ++ seqOfFiles(targetLibDir) ++ seqOfFiles(targetBinDir))
                    .map(f => f.getAbsoluteFile)
                }

                val installedArtefactDetails = try {
                  JacksMapper.readValue[InstalledArtefactDetails](
                    using(Source.fromFile(
                      workingDirectory.sub("%s.%s.%s.json".format(arguments(1), arguments(2), arguments(3))))) {
                      source =>
                        source.getLines().mkString
                    })
                } catch {
                  case _: Throwable => new InstalledArtefactDetails("")
                }

                it("Then the endpoint was requested") {
                  provider.verify()
                }
                it("Then the git update was invoked") {
                  Mockito.verify(mockProcessBuilderFactory).apply(Seq[String](programs.git, "pull"))
                  Mockito.verify(mockGitProcessBuilder._1).directory(cacheDirectory.sub(arguments(1) + "/source"))
                  Mockito.verify(mockGitProcessBuilder._1).start()
                }
                it("Then the git version is retrieved") {
                  Mockito.verify(mockProcessBuilderFactory).apply(Seq[String](programs.git, "rev-parse", "HEAD"))
                  Mockito.verify(mockGitVersionProcessBuilder._1).directory(cacheDirectory.sub(arguments(1) + "/source"))
                  Mockito.verify(mockGitVersionProcessBuilder._1).start()
                  Mockito.verify(mockGitVersionProcessBuilder._2).waitFor()
                }

                val cmakeSourceTmpPath = temporaryDirectory.file
                it("Then the cmake process was invoked") {
                  val cmakeSourcePath = new File(cacheDirectory.file, arguments(1) + "/source")

                  Mockito.verify(mockProcessBuilderFactory).apply(Seq[String](programs.cmake,
                    cmakeSourcePath.getAbsolutePath,
                    "-G\"MinGW", "Makefiles\"",
                    "-DCMAKE_BUILD_TYPE=" + cmakeBuildType,
                    "-DCMAKE_INSTALL_PREFIX=" + new File(cacheDirectory.file, arguments(1) + "/" + arguments(2) + "." + mode).getAbsolutePath
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
                  val expectedWorkingDirectoryFiles = expectedFiles.map(filename => workingDirectory.sub(filename))
                  assert(installedFiles.diff(expectedWorkingDirectoryFiles).size == 0)
                }
                it("Then the local artefacts were tagged with the git version") {
                  assert(installedArtefactDetails.version == updatedArtefactVersion)
                }
            }
          }
      }
    }
  }

  testCase("debug", "Debug")
  testCase("release", "Release")
}