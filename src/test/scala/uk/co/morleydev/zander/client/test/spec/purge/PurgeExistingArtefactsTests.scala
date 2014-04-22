package uk.co.morleydev.zander.client.test.spec.purge

import uk.co.morleydev.zander.client.util.Using.using
import uk.co.morleydev.zander.client.test.spec.util.TestHarnessSpec
import uk.co.morleydev.zander.client.test.gen.GenNative
import java.io.File

class PurgeExistingArtefactsTests extends TestHarnessSpec {
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
            .whenArtefactsAreLocallyInstalled(expectedFiles = expectedFiles)
            .invokeMain()
            .thenTheExpectedFilesWereRemovedLocally(expectedFiles)
            .thenTheLocalArtefactsWereNotTaggedWithDetails()
      }
    }
  }
}
