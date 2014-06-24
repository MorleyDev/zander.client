package uk.co.morleydev.zander.client.test.spec.purge

import uk.co.morleydev.zander.client.util.using
import uk.co.morleydev.zander.client.test.gen.GenNative
import java.io.File
import uk.co.morleydev.zander.client.test.spec.{SpecTest, ResponseCodes}

class PurgeExistingArtefactsTests extends SpecTest {
  describe("Given locally installed artefacts") {
    describe("When purging") {
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

      using(this.start()) {
        harness =>
          harness.whenPurging()
            .whenTheArtefactsAreLocallyInstalled(expectedFiles = expectedFiles)
            .invokeMain()
            .thenTheExpectedFilesWereRemovedLocally(expectedFiles)
            .thenTheLocalArtefactsWereNotTaggedWithDetails()
            .thenExpectedResponseCodeWasReturned(ResponseCodes.Success)
      }
    }
  }
}
