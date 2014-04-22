package uk.co.morleydev.zander.client.test.spec.install

import uk.co.morleydev.zander.client.test.gen.GenNative
import uk.co.morleydev.zander.client.util.Using._
import uk.co.morleydev.zander.client.test.spec.ResponseCodes
import uk.co.morleydev.zander.client.test.spec.util.TestHarnessSpec
import java.io.File

class InstallCachedSourceExistsCachedArtefactsDoNotExistTests extends TestHarnessSpec {

  override def cmakeTestCase(compiler : String, mode : String, cmakeBuildType: String, generator: String) = {

    describe("Given the project/compiler endpoint exists and the cache already contains the source but no artefacts") {
      describe("When install is carried out for %s.%s".format(compiler, mode)) {

        using(this.start()) {
          testHarness =>
            val artefactVersion = GenNative.genAlphaNumericString(10, 100)

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
              .givenGitIsPossible(artefactVersion)
              .givenFullCMakeBuildIsPossible(expectedFiles)
              .whenInstalling(compiler = compiler, mode = mode)
              .whenTheCacheAlreadyContainsTheSourceCode()
              .expectSuccessfulRequest(gitUrl)
              .invokeMain()
              .thenTheExpectedServerRequestsWereHandled()
              .thenAGitUpdateWasInvoked()
              .thenTheGitVersionWasRetrieved()
              .thenACMakePreBuildWasInvoked(cmakeBuildType, generator)
              .thenACMakeBuildWasInvoked(cmakeBuildType)
              .thenAMakeInstallWasInvoked(cmakeBuildType)
              .thenExpectedResponseCodeWasInstalled(ResponseCodes.Success)
              .thenTheLocalArtefactsWereTaggedWithTheExpectedVersion(artefactVersion)
              .thenTheLocalArtefactsWereTaggedWithTheExpectedFiles(expectedFiles)
              .thenTheExpectedFilesWereInstalledLocally(expectedFiles)
        }
      }
    }
  }

  runAllTestCmakeCases()
}
