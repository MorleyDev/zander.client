package uk.co.morleydev.zander.client.test.spec.update

import uk.co.morleydev.zander.client.util.using
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

  describe("Given locally installed artefacts") {
    describe("When no server") {
      using(this.start()) {
        harness =>
          harness.whenUpdating()
            .whenTheArtefactsAreLocallyInstalled()
            .invokeMain()
            .thenExpectedResponseCodeWasReturned(ResponseCodes.EndpointNotFound)
      }
    }
  }

  describe("Given locally installed artefacts") {
    describe("When server but the endpoint does not exist") {
      using(this.start()) {
        harness =>
          harness.givenAServer()
            .whenUpdating()
            .expectRequestToArgumentEndpointThenReplyWith(404, "{ }")
            .whenTheArtefactsAreLocallyInstalled()
            .invokeMain()
            .thenTheExpectedServerRequestsWereHandled()
            .thenExpectedResponseCodeWasReturned(ResponseCodes.EndpointNotFound)
      }
    }
  }
}
