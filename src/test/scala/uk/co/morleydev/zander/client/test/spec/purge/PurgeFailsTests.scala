package uk.co.morleydev.zander.client.test.spec.purge

import uk.co.morleydev.zander.client.util.using
import uk.co.morleydev.zander.client.test.spec.{SpecTest, ResponseCodes}

class PurgeFailsTests extends SpecTest {
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
