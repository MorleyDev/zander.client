package uk.co.morleydev.zander.client.test.spec

import uk.co.morleydev.zander.client.test.gen.{GenStringArguments, GenNative}
import uk.co.morleydev.zander.client.util.Using._

class AnyTests extends SpecTest {

  describe("Given Zander When running with an invalid number of arguments") {
    using(this.start()) {
      harness =>
        harness
          .whenRanWithArguments(GenNative.genSequence(0, 3, () => GenNative.genAlphaNumericString(1, 10)).toArray)
          .invokeMain()
          .thenExpectedResponseCodeWasReturned(ResponseCodes.InvalidArgumentCount)
    }
  }

  describe("Given Zander When running an invalid operation") {
    using(this.start()) {
      harness =>
        val invalidOperation = GenNative.genAlphaNumericStringExcluding(1, 10, GenStringArguments.operations)
        harness
          .whenExecutingOperation(invalidOperation)
          .invokeMain()
          .thenExpectedResponseCodeWasReturned(ResponseCodes.InvalidOperation)
    }
  }

  describe("Given Zander When running an invalid compiler") {
    using(this.start()) {
      harness =>
        val invokedCompiler = GenNative.genAlphaNumericStringExcluding(1, 10, GenStringArguments.compilers)
        harness
          .whenExecutingOperation(compiler = invokedCompiler)
          .invokeMain()
          .thenExpectedResponseCodeWasReturned(ResponseCodes.InvalidCompiler)
    }
  }

  describe("Given Zander when running an invalid build mode") {
    using(this.start()) {
      harness =>
        val invalidBuildMode = GenNative.genAlphaNumericStringExcluding(1, 10, GenStringArguments.buildModes)
        harness
          .whenExecutingOperation(mode = invalidBuildMode)
          .invokeMain()
          .thenExpectedResponseCodeWasReturned(ResponseCodes.InvalidBuildMode)
    }
  }

  describe("Given Zander when running any operation with a non-alphanumeric project") {
    using(this.start()) {
      harness =>
        val invalidProject = Iterator.continually(GenNative.genAsciiString(1, 20))
          .find(f => !f.forall(c => c.isLetterOrDigit))
          .get

        harness
          .whenExecutingOperation(project = invalidProject)
          .invokeMain()
          .thenExpectedResponseCodeWasReturned(ResponseCodes.InvalidProject)
    }
  }

  describe("Given Zander when running any operation with a project of invalid length") {
    using(this.start()) {
      harness =>
        val invalidProject = GenNative.genAlphaNumericString(21, 50)
        harness
          .whenExecutingOperation(project = invalidProject)
          .invokeMain()
          .thenExpectedResponseCodeWasReturned(ResponseCodes.InvalidProject)
    }
  }
}
