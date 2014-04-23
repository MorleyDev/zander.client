package uk.co.morleydev.zander.client.test.spec.install

import uk.co.morleydev.zander.client.test.spec.{SpecTest, ResponseCodes}
import uk.co.morleydev.zander.client.util.Using.using

class InstallFailsTests extends SpecTest {

  describe("Given a server does not exist") {
    describe("When an install operation is carried out") {
      using(this.start()) {
        harness =>
          harness.whenInstalling()
            .invokeMain()
            .thenExpectedResponseCodeWasReturned(ResponseCodes.EndpointNotFound)
      }
    }
  }

  describe("Given a server but the endpoint does not exist") {
    describe("When an install operation is carried out") {
      using(this.start()) {
        harness =>
          harness.givenAServer()
            .whenInstalling()
            .expectRequestToArgumentEndpointThenReplyWith(404, "{ }")
            .invokeMain()
            .thenExpectedResponseCodeWasReturned(ResponseCodes.EndpointNotFound)
      }
    }
  }

  describe("Given locally installed artefacts") {
    describe("When an install operation is carried out that matches those artefacts") {
      using(this.start()) {
        harness =>
          harness.whenInstalling()
            .whenTheArtefactsAreLocallyInstalled()
            .invokeMain()
            .thenExpectedResponseCodeWasReturned(ResponseCodes.ArtefactsAlreadyInstalled)
      }
    }
  }
}
