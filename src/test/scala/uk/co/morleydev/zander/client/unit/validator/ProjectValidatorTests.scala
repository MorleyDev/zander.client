package uk.co.morleydev.zander.client.unit.validator

import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.validator.{Validator, ProjectValidator}
import uk.co.morleydev.zander.client.validator.exceptions.InvalidProjectException
import org.scalacheck.Gen
import scala.util.Random

class ProjectValidatorTests extends FunSpec {

  describe("Given a project validator") {

    val validator : Validator[String] = ProjectValidator

    Array[String]("p", "pro", "2sad", "as34dfg9", "123456789", "as34dfgrt12adaf", "a23456k890p23jhggfds").foreach({ project =>
      describe("When validating a valid project that is an alphanumeric string of length " + project.size) {
        var thrownException : Exception = null
        try {
          validator.validate(project)
        } catch {
          case e : Exception => thrownException = e
        }
        it("Then no exception was thrown") {
          assert(thrownException == null)
        }
      }
    })

      Gen.alphaStr.suchThat(_.size > 20).sample.foreach({ project =>
      describe("When validating a project with invalid alphanumeric length greater than 20 (" + project + ")") {
        var thrownException : Exception = null
        try {
          validator.validate(project)
        } catch {
          case e : Exception => thrownException = e
        }
        it("Then an exception was thrown") {
          assert(thrownException != null)
        }
        it("Then the expected exception was thrown") {
          assert(thrownException.isInstanceOf[InvalidProjectException])
        }
      }
    })

    describe("When validating a project with an invalid string of length 0") {
      var thrownException : Exception = null
      try {
        validator.validate("")
      } catch {
        case e : Exception => thrownException = e
      }
      it("Then an exception was thrown") {
        assert(thrownException != null)
      }
      it("Then the expected exception was thrown") {
        assert(thrownException.isInstanceOf[InvalidProjectException])
      }
    }

    val random = new Random()
    Iterator.continually(random.nextString(random.nextInt(21)))
            .filter(f => f.count(c => !c.isLetterOrDigit) > 1)
            .take(20).foreach({
      project => describe("When validating a project with string containing invalid character: " + project) {
        var thrownException : Exception = null
        try {

          validator.validate(project)
        } catch {
          case e : Exception => thrownException = e
        }
        it("Then an exception was thrown") {
          assert(thrownException != null)
        }
        it("Then the expected exception was thrown") {
          assert(thrownException.isInstanceOf[InvalidProjectException])
        }
      }
    })
  }
}
