package uk.co.morleydev.zander.client.unit.util

import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.util.Using

class UsingTests extends FunSpec {

  describe("Given an autoclosable") {
    val closable = new AutoCloseable {
      var wasClosed = false
      def close() : Unit = {
        wasClosed = true
      }
    }

    describe("When using a using block") {
      var actualClosable : AutoCloseable = null
      Using.using(closable) { c =>
        actualClosable = c
      }

      it("Then the expected autoclosable was captured") {
        assert(actualClosable == closable)
      }
      it("Then the autoclosable was closed") {
        assert(closable.wasClosed == true)
      }
    }
  }
}
