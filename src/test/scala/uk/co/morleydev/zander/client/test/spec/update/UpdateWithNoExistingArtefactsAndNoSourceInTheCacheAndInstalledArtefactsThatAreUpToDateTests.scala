package uk.co.morleydev.zander.client.test.spec.update

import uk.co.morleydev.zander.client.test.spec.{ResponseCodes, SpecTest}
import uk.co.morleydev.zander.client.util.using
import uk.co.morleydev.zander.client.test.gen.GenNative
import java.io.File

class UpdateWithNoExistingArtefactsAndNoSourceInTheCacheAndInstalledArtefactsThatAreUpToDateTests extends SpecTest {
  override def cmakeTestCase(compiler : String, mode: String, cmakeBuildType: String, generator: String) = {
    describe("Given the project/compiler endpoint exists and the cache contains no source and no artefacts") {
      describe("When up-to-date artefacts are installed and update is carried out for %s.%s".format(compiler, mode)) {

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
              .givenFullGitPipelineIsPossible(artefactVersion)
              .givenFullCMakePipelineIsPossible(expectedFiles)
              .whenUpdating(compiler = compiler, mode = mode)
              .whenTheArtefactsAreLocallyInstalled(artefactVersion, expectedFiles)
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
