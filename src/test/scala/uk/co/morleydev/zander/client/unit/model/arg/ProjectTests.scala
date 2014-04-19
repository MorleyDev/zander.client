package uk.co.morleydev.zander.client.unit.model.arg

import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.gen.GenNative
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalacheck.Gen

class ProjectTests extends FunSpec with GeneratorDrivenPropertyChecks {

  private val validProjectCharacters = GenNative.alphaNumericCharacters ++ Seq[Char]('.', '_', '-')
  private val invalidProjectCharacters = (0.toChar to 255.toChar).diff(validProjectCharacters)

  describe("Given a project string") {
    describe("When initialising a valid project that is an alphanumeric string") {

      val validProjectGenerator: Gen[Project] =
        Gen.oneOf(Iterator.continually(GenNative.genStringContaining(1, 20, validProjectCharacters))
          .map(new Project(_))
          .take(100)
          .toSeq)

      it("Then the project can be compared to another project") {
        forAll(validProjectGenerator) {
          project: Project => {

            assert(project != new Project(GenNative.genAlphaNumericStringExcluding(1, 20, Seq[String](project.value))))
            assert(project == new Project(project.value))
          }
        }
      }
      it("Then toString gives the expected string") {
        forAll(validProjectGenerator) {
          project =>

            assert(project != new Project(GenNative.genAlphaNumericStringExcluding(1, 20, Seq[String](project.value))))
            assert(project == new Project(project.value))
        }
      }
    }

    describe("When initialising a project with invalid alphanumeric length greater than 20") {
      var thrownException: Exception = null
      try {
        new Project(GenNative.genStringContaining(21, 100, validProjectCharacters))
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

    val invalidCharacterProjectStringGenerator : Gen[String] =
      Gen.oneOf(Iterator.continually(GenNative.genStringContaining(1, 20, (0 to 255).map(_.toChar)))
        .filter(c => c.count(invalidProjectCharacters.contains(_)) > 1)
        .take(100)
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
  }
}