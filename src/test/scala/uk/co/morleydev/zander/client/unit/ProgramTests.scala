package uk.co.morleydev.zander.client.unit

import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.Program
import uk.co.morleydev.zander.client.model.arg.{Operation, Compiler, BuildMode}
import uk.co.morleydev.zander.client.model.{Arguments, Configuration}
import uk.co.morleydev.zander.client.validator.Validator
import org.scalatest.mock.MockitoSugar
import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.validator.exceptions.InvalidProjectException

class ProgramTests extends FunSpec with MockitoSugar {

  describe("Given a Program When running the program") {

      val mockValidator = mock[Validator[String]]
      val program = new Program(mockValidator)

      val expectedProjectName = "project"
      val args = new Arguments(Operation.Install, expectedProjectName, Compiler.GnuCxx, BuildMode.Debug)
      val config = new Configuration("http://localhost")
      val responseCode = program.run(args, config)

      it("Then the project is validated") {
        Mockito.verify(mockValidator).validate(expectedProjectName)
      }
      it("Then the response code is -404") {
        assert(responseCode == -404)
      }
    }

    describe("Given a Program when running the program and validation of the project fails") {

      val mockValidator = mock[Validator[String]]
      val program = new Program(mockValidator)

      Mockito.when(mockValidator.validate(Matchers.anyString()))
             .thenThrow(new InvalidProjectException())

      val args = new Arguments(Operation.Install, "project", Compiler.GnuCxx, BuildMode.Debug)
      val config = new Configuration("http://localhost")
      val responseCode = program.run(args, config)

      it("Then the response code is 2") {
        assert(responseCode == 2)
      }
    }
}
