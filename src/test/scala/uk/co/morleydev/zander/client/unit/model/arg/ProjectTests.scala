package uk.co.morleydev.zander.client.unit.model.arg

import org.scalatest.FunSpec
import scala.util.Random
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.gen.GenNative

class ProjectTests extends FunSpec {

  private val random = new Random()

  describe("Given a project string") {

    Iterator.continually(random.alphanumeric.take(random.nextInt(20)+1))
      .take(20)
      .toList
      .distinct
      .map(chars => chars.mkString)
      .foreach({ projectCode =>
      describe("When initialising a valid project that is an alphanumeric string " + projectCode  + " of length " + projectCode.size) {
        val project : Project = new Project(projectCode)

        it("Then the project can be compared to another project") {
          assert(!project.equals(new Project(GenNative.genAlphaNumericStringExcluding(1, 20, Seq[String](projectCode)))))
          assert(project.equals(new Project(projectCode)))
        }
        it("Then toString gives the expected string") {
          assert(project.toString == projectCode)
        }
      }
    })

    Iterator.continually(random.alphanumeric.take(random.nextInt(22)+21))
      .take(20)
      .map(chars => chars.mkString)
      .foreach({ project =>
      describe("When initialising a project with invalid alphanumeric length " + project.size + " greater than 20 (" + project + ")") {
        var thrownException : Exception = null
        try {
          new Project(project)
        } catch {
          case e : Exception => thrownException = e
        }
        it("Then an exception was thrown") {
          assert(thrownException != null)
        }
        it("Then the expected exception was thrown") {
          assert(thrownException.isInstanceOf[IllegalArgumentException])
        }
      }
    })

    describe("Given a project string containing - _ and alphanumeric characters") {
      val projectCode ="H-H_allowThese"
      it("When initialising the project then no exception is thrown") {
        new Project(projectCode)
      }
    }

    describe("When initialising a project with an invalid string of length 0") {
      var thrownException : Exception = null
      try {
        new Project("")
      } catch {
        case e : Exception => thrownException = e
      }
      it("Then an exception was thrown") {
        assert(thrownException != null)
      }
      it("Then the expected exception was thrown") {
        assert(thrownException.isInstanceOf[IllegalArgumentException])
      }
    }

    Iterator.continually(random.nextString(random.nextInt(20)+1))
      .filter(f => f.count(c => !c.isLetterOrDigit) > 1)
      .take(20)
      .toList
      .distinct
      .foreach({
      project => describe("When initialising a project with string containing invalid characters: " + project) {
        var thrownException : Exception = null
        try {

          new Project(project)
        } catch {
          case e : Exception => thrownException = e
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
