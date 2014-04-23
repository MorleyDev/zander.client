package uk.co.morleydev.zander.client.test.spec.update

import uk.co.morleydev.zander.client.util.Using._
import uk.co.morleydev.zander.client.test.spec.{SpecTest, ResponseCodes}

class UpdateFailsTests extends SpecTest {
  describe("Given no local artefacts are installed") {
    describe("When updating local artefacts") {
      using(this.start()) {
        harness =>
          harness.whenUpdating()
            .invokeMain()
            .thenExpectedResponseCodeWasReturned(ResponseCodes.ArtefactsNotInstalled)
      }
    }
  }
}
