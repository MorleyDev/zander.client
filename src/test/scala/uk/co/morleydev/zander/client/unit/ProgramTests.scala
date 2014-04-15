package uk.co.morleydev.zander.client.unit

import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.Program
import uk.co.morleydev.zander.client.model.arg.{Operation, Compiler, BuildMode}
import uk.co.morleydev.zander.client.model.{Arguments, Configuration}
import uk.co.morleydev.zander.client.validator.Validator

class ProgramTests extends FunSpec {

  describe("Given a Program") {

    var mockValidatorValue = ""
    val mockValidator = new Validator[String] {
      def validate(value: String): Unit = { mockValidatorValue = value }
    }
    val program = new Program(mockValidator)

    describe("When running the program") {
      val expectedProjectName = "project"
      val args = new Arguments(Operation.Install, expectedProjectName, Compiler.GnuCxx, BuildMode.Debug)
      val config = new Configuration("http://localhost")
      val responseCode = program.run(args, config)

      it("Then the project is validated") {
        assert(mockValidatorValue == expectedProjectName)
      }
      it("Then the response code is -404") {
        assert(responseCode == -404)
      }
    }
  }
}
