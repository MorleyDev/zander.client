package uk.co.morleydev.zander.client.unit.model.arg

import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.gen.GenNative

class ProjectTests extends FunSpec {

  private val validProjectCharacters = GenNative.alphaNumericCharacters ++ Seq[Char]('.', '_', '-')

  describe("Given a project string") {

    Iterator.continually(GenNative.genStringContaining(1, 20, validProjectCharacters))
      .take(100)
      .toList.distinct
      .foreach({
      projectCode =>
        describe("When initialising a valid project that is an alphanumeric string " + projectCode + " of length " + projectCode.size) {
          val project = new Project(projectCode)

          it("Then the project can be compared to another project") {
            assert(!project.equals(new Project(GenNative.genAlphaNumericStringExcluding(1, 20, Seq[String](projectCode)))))
            assert(project.equals(new Project(projectCode)))
          }
          it("Then toString gives the expected string") {
            assert(project.toString == projectCode)
          }
        }
    })

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

    Iterator.continually(GenNative.genStringContaining(1, 19, validProjectCharacters))
      .take(100)
      .map(s => s + GenNative.genOneFrom((0.toChar to 255.toChar).diff(validProjectCharacters)))
      .toList.distinct
      .foreach(project => {
      describe("When initialising a project string " + project + " containing invalid characters") {
        var thrownException: Exception = null
        try {
          new Project(project)
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
    })
  }
}