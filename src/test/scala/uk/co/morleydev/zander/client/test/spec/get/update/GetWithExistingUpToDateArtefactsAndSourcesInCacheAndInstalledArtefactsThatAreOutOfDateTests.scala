package uk.co.morleydev.zander.client.test.spec.get.update

import java.io.File
import uk.co.morleydev.zander.client.test.gen.GenNative
import uk.co.morleydev.zander.client.test.spec.{ResponseCodes, SpecTest}
import uk.co.morleydev.zander.client.util.using

class GetWithExistingUpToDateArtefactsAndSourcesInCacheAndInstalledArtefactsThatAreOutOfDateTests extends SpecTest {
  override def noBuildTestCase(compiler : String, mode: String) = {
    describe("Given the project/compiler endpoint exists and the cache already contains the source but no artefacts") {
      describe("When out-of-date artefacts are installed and get is carried out for %s.%s".format(compiler, mode)) {

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

            val expectedRemovedArtefacts = Seq[String]("include/subdir_other/" + GenNative.genAlphaNumericString(1, 20),
              "lib/subdir_other/" + GenNative.genAlphaNumericString(1, 20),
              "bin/subdir_other/" + GenNative.genAlphaNumericString(1, 20))

            testHarness
              .givenAServer()
              .givenFullGitPipelineIsPossible(artefactVersion)
              .whenGetting(compiler = compiler, mode = mode)
              .whenTheCacheAlreadyContainsTheSourceCode()
              .whenTheCacheAlreadyContainsArtefacts(artefactVersion, expectedFiles)
              .whenTheArtefactsAreLocallyInstalled(GenNative.genAlphaNumericStringExcluding(1, 20, Seq[String](artefactVersion)),
                expectedFiles ++ expectedRemovedArtefacts)
              .expectSuccessfulRequest(gitUrl)
              .invokeMain()
              .thenTheExpectedServerRequestsWereHandled()
              .thenAGitUpdateWasInvoked()
              .thenAGitCheckoutWasInvoked()
              .thenTheGitVersionWasRetrieved()
              .thenExpectedResponseCodeWasReturned(ResponseCodes.Success)
              .thenTheLocalArtefactsWereTaggedWithTheExpectedVersion(artefactVersion)
              .thenTheLocalArtefactsWereTaggedWithTheExpectedFiles(expectedFiles)
              .thenTheExpectedFilesWereInstalledLocally(expectedFiles)
              .thenTheExpectedFilesWereRemovedLocally(expectedRemovedArtefacts)
        }
      }
    }
  }

  runAllTestNoBuildCases()
}
