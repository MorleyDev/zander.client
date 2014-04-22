package uk.co.morleydev.zander.client.test.spec.purge

import uk.co.morleydev.zander.client.util.Using.using
import uk.co.morleydev.zander.client.test.spec.util.TestHarnessSpec
import uk.co.morleydev.zander.client.test.spec.ResponseCodes

class PurgeFailsTests extends TestHarnessSpec {
  describe("Given no local artefacts are installed") {
    describe("When purging local artefacts") {
      using(this.start()) {
        harness =>
          harness.whenPurging()
                 .invokeMain()
                 .thenExpectedResponseCodeWasReturned(ResponseCodes.ArtefactsNotInstalled)
      }
    }
  }
}
