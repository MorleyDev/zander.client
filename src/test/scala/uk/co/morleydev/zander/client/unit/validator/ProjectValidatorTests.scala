package uk.co.morleydev.zander.client.unit.validator

import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.validator.{Validator, ProjectValidator}
import uk.co.morleydev.zander.client.validator.exceptions.InvalidProjectException
import org.scalacheck.Gen
import scala.util.Random

class ProjectValidatorTests extends FunSpec {

  private val random = new Random()

  describe("Given a project validator") {

    val validator : Validator[String] = ProjectValidator

      Iterator.continually(random.alphanumeric.take(random.nextInt(20)+1))
              .take(20)
              .map(chars => chars.mkString)
              .foreach({ project =>
      describe("When validating a valid project that is an alphanumeric string " + project  + " of length " + project.size) {
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

    Iterator.continually(random.alphanumeric.take(random.nextInt(22)+21))
      .take(20)
      .map(chars => chars.mkString)
      .foreach({ project =>
      describe("When validating a project with invalid alphanumeric length " + project.size + " greater than 20 (" + project + ")") {
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

    Iterator.continually(random.nextString(random.nextInt(20)+1))
            .filter(f => f.count(c => !c.isLetterOrDigit) > 1)
            .take(20)
            .toList
            .distinct
            .foreach({
      project => describe("When validating a project with string containing invalid characters: " + project) {
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
