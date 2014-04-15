package uk.co.morleydev.zander.client.unit.util

import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.util.Using

class UsingTests extends FunSpec {

  describe("Given an autoclosable") {
    class MockAutoCloseable {
      var wasClosed = false
      def close() : Unit = {
        wasClosed = true
      }
    }
    describe("When using a using block") {
      val closable = new MockAutoCloseable()

      var actualClosable : MockAutoCloseable = null
      Using.using(closable) { c =>
        actualClosable = c
      }

      it("Then the expected autoclosable was captured") {
        assert(actualClosable == closable)
      }
      it("Then the autoclosable was closed") {
        assert(closable.wasClosed)
      }
    }
    describe("When using a using block that throws") {

      val expectedException = new Exception()
      var actualException : Exception = null
      val closable = new MockAutoCloseable()
      try {
        Using.using(closable) { c =>
          throw expectedException
        }
      } catch {
        case e : Exception => actualException = e
      }

      it("Then the expected exception was thrown") {
        assert(actualException == expectedException)
      }
      it("Then the autoclosable was closed") {
        assert(closable.wasClosed)
      }
    }
  }
}
