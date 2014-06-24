package uk.co.morleydev.zander.client.test.unit.model.arg

import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.test.gen.{GenStringArguments, GenNative}
import uk.co.morleydev.zander.client.test.unit.UnitTest

class ProjectTests extends UnitTest with GeneratorDrivenPropertyChecks {

  implicit override val generatorDrivenConfig = PropertyCheckConfig(minSize = 10, maxSize = 20)

  describe("Given a project string") {
    describe("When initialising a valid project that is an alphanumeric string") {

      val validProjectGenerator: Gen[Project] =
        Gen.oneOf(Iterator.continually(GenStringArguments.genProject())
          .map(new Project(_)).take(20).toSeq)

      it("Then the project can be compared to another project") {
        forAll(validProjectGenerator) {
          project: Project => {

            assert(project != new Project(GenNative.genAlphaNumericStringExcluding(2, 20, Seq[String](project.value))))
            assert(project == new Project(project.value))
          }
        }
      }
      it("Then toString gives the expected string") {
        forAll(validProjectGenerator) {
          project =>

            assert(project != new Project(GenNative.genAlphaNumericStringExcluding(2, 20, Seq[String](project.value))))
            assert(project == new Project(project.value))
        }
      }
    }

    describe("When initialising a project with invalid alphanumeric length greater than 20") {
      var thrownException: Exception = null
      try {
        new Project(GenStringArguments.genInvalidProjectWithTooLargeLength())
      } catch {
        case e: Exception => thrownException = e
      }
      it("Then an exception was thrown") {
        assert(thrownException != null)
      }
      it("Then the expected exception was thrown") {
        assert(thrownException.isInstanceOf[IllegalArgumentException])
      }
    }

    describe("When initialising a project with an invalid string of length 0") {
      var thrownException: Exception = null
      try {
        new Project("")
      } catch {
        case e: Exception => thrownException = e
      }
      it("Then an exception was thrown") {
        assert(thrownException != null)
      }
      it("Then the expected exception was thrown") {
        assert(thrownException.isInstanceOf[IllegalArgumentException])
      }
    }

    val invalidCharacterProjectStringGenerator: Gen[String] =
      Gen.oneOf(Iterator.continually(GenStringArguments.genInvalidProjectWithBannedCharacters())
        .take(20)
        .toSeq)

    describe("When initialising a project string containing invalid characters") {

      it("Then the expected exception was thrown") {
        forAll(invalidCharacterProjectStringGenerator) { project =>
          var thrownException: Exception = null
          try {
            new Project(project)
          } catch {
            case e: Exception => thrownException = e
          }
          assert(thrownException.isInstanceOf[IllegalArgumentException])
        }
      }
    }

    describe("When initialising a project string of only \".\"") {
      it("Then the expected exception was thrown") {
        var thrownException: Exception = null
        try {
          new Project(".")
        } catch {
          case e: Exception => thrownException = e
        }
        assert(thrownException.isInstanceOf[IllegalArgumentException])
      }
    }
  }
}