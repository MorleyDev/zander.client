package uk.co.morleydev.zander.client.test.spec.install

import uk.co.morleydev.zander.client.util.using
import uk.co.morleydev.zander.client.test.gen.GenNative
import uk.co.morleydev.zander.client.test.spec.{SpecTest, ResponseCodes}
import java.io.File

class InstallCachedSourceDoesNotExistCachedArtefactsDoExistAndAreOutOfDateTests extends SpecTest {

  override def cmakeTestCase(compiler : String, mode: String, cmakeBuildType: String, generator: String) = {
    describe("Given the project/compiler endpoint exists and the cache already out of date artefacts but no source") {
      describe("When install is carried out for %s.%s".format(compiler, mode)) {

        using(this.start()) {
          testHarness =>
            val artefactVersion = GenNative.genAlphaNumericString(10, 100)
            val oldArtefactVersion = GenNative.genAlphaNumericStringExcluding(10, 100, Seq[String](artefactVersion))

            val gitUrl = "http://git_url/request/at_me"

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
              .map(s => new File(s).toString)

            testHarness
              .givenAServer()
              .givenFullGitPipelineIsPossible(artefactVersion)
              .givenFullCMakePipelineIsPossible(expectedFiles)
              .whenInstalling(compiler = compiler, mode = mode)
              .whenTheCacheAlreadyContainsArtefacts(oldArtefactVersion, expectedFiles ++ Seq[String]("include/gone"))
              .expectSuccessfulRequest(gitUrl)
              .invokeMain()
              .thenTheExpectedServerRequestsWereHandled()
              .thenAGitCloneWasInvoked(gitUrl)
              .thenAGitCheckoutWasInvoked()
              .thenTheGitVersionWasRetrieved()
              .thenACMakePreBuildWasInvoked(cmakeBuildType, generator)
              .thenACMakeBuildWasInvoked(cmakeBuildType)
              .thenACMakeInstallWasInvoked(cmakeBuildType)
              .thenExpectedResponseCodeWasReturned(ResponseCodes.Success)
              .thenTheLocalArtefactsWereTaggedWithTheExpectedVersion(artefactVersion)
              .thenTheLocalArtefactsWereTaggedWithTheExpectedFiles(expectedFiles)
              .thenTheExpectedFilesWereInstalledLocally(expectedFiles)
        }
      }
    }
  }

  runSingleCmakeCase()
}
