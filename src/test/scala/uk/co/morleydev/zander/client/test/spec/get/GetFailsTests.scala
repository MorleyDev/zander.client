package uk.co.morleydev.zander.client.test.spec.get

import uk.co.morleydev.zander.client.util.Using.using
import uk.co.morleydev.zander.client.test.spec.{ResponseCodes, SpecTest}

class GetFailsTests extends SpecTest {
  describe("Given no server and no local artefacts") {
    describe("When get") {
      using(this.start()) {
        harness =>
          harness.whenGetting()
            .invokeMain()
            .thenExpectedResponseCodeWasReturned(ResponseCodes.EndpointNotFound)
      }
    }
  }
  describe("Given no server and local artefacts") {
    describe("When get") {
      using(this.start()) {
        harness =>
          harness.whenGetting()
            .whenTheArtefactsAreLocallyInstalled()
            .invokeMain()
            .thenExpectedResponseCodeWasReturned(ResponseCodes.EndpointNotFound)
      }
    }
  }
  describe("Given server and local artefacts but endpoint does not exist") {
    describe("When get") {
      using(this.start()) {
        harness =>
          harness.givenAServer()
            .whenGetting()
            .expectRequestToArgumentEndpointThenReplyWith(404, "{ }")
            .whenTheArtefactsAreLocallyInstalled()
            .invokeMain()
            .thenExpectedResponseCodeWasReturned(ResponseCodes.EndpointNotFound)
      }
    }
  }
  describe("Given server and no local artefacts but endpoint does not exist") {
    describe("When get") {
      using(this.start()) {
        harness =>
          harness.givenAServer()
            .whenGetting()
            .expectRequestToArgumentEndpointThenReplyWith(404, "{ }")
            .invokeMain()
            .thenExpectedResponseCodeWasReturned(ResponseCodes.EndpointNotFound)
      }
    }
  }
}
