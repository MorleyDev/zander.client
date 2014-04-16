package uk.co.morleydev.zander.client.unit

import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.Program
import uk.co.morleydev.zander.client.model.arg.Compiler
import uk.co.morleydev.zander.client.model.arg.Compiler.Compiler
import uk.co.morleydev.zander.client.model.{ExitCodes, Arguments, Configuration}
import uk.co.morleydev.zander.client.validator.Validator
import org.scalatest.mock.MockitoSugar
import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.validator.exceptions.InvalidProjectException
import uk.co.morleydev.zander.client.check.GenNative
import uk.co.morleydev.zander.client.controller.{Controller, ControllerFactory}
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.BuildMode
import uk.co.morleydev.zander.client.model.arg.Operation.Operation
import uk.co.morleydev.zander.client.model.arg.Operation
import uk.co.morleydev.zander.client.data.net.exceptions.ProjectNotFoundException

class ProgramTests extends FunSpec with MockitoSugar {

  val expectedCompiler = GenNative.genOneFrom(Compiler.values.toSeq)
  val expectedBuildMode = GenNative.genOneFrom(BuildMode.values.toSeq)
  val expectedProjectName = GenNative.genAlphaNumericString(1, 20)
  
  describe("Given a Program When running the program") {

    val mockValidator = mock[Validator[String]]
    val mockControllerFactory = mock[ControllerFactory]
    val mockInstallController = mock[Controller]

    Mockito.when(mockControllerFactory.createInstallController(Matchers.any[Configuration]()))
           .thenReturn(mockInstallController)

    val program = new Program(mockValidator, mockControllerFactory)

    val expectedOperation = Operation.Install
    val args = new Arguments(expectedOperation, expectedProjectName, expectedCompiler, expectedBuildMode)
    val config = new Configuration("http://localhost")
    val responseCode = program.run(args, config)

    it("Then the project is validated") {
      Mockito.verify(mockValidator).validate(expectedProjectName)
    }
    it("Then the install controller is created") {
      Mockito.verify(mockControllerFactory).createInstallController(config)
    }
    it("Then the install controller is invoked") {
      Mockito.verify(mockInstallController).apply(expectedOperation, expectedProjectName, expectedCompiler, expectedBuildMode)
    }
    it("Then the response code is returned") {
      assert(responseCode == ExitCodes.Success)
    }
  }

  def givenProgramWhenRunningControllerThrowsThenExpectedStatusCode(e : Throwable, c : Int) = {
    describe("Given a Program When running the program and the controller throws a " + e.getClass.getSimpleName) {

      val mockValidator = mock[Validator[String]]
      val mockControllerFactory = mock[ControllerFactory]
      val mockInstallController = mock[Controller]

      Mockito.when(mockControllerFactory.createInstallController(Matchers.any[Configuration]()))
        .thenReturn(mockInstallController)
      Mockito.when(mockInstallController.apply(Matchers.any[Operation](), Matchers.any[String](), Matchers.any[Compiler](), Matchers.any[BuildMode]()))
        .thenThrow(e)

      val program = new Program(mockValidator, mockControllerFactory)

      val expectedOperation = Operation.Install
      val args = new Arguments(expectedOperation, expectedProjectName, expectedCompiler, expectedBuildMode)
      val config = new Configuration("http://localhost")
      val responseCode = program.run(args, config)

      it("Then the expected response code is returned") {
        assert(responseCode == c)
      }
    }
  }
  givenProgramWhenRunningControllerThrowsThenExpectedStatusCode(new RuntimeException, ExitCodes.UnknownError)
  givenProgramWhenRunningControllerThrowsThenExpectedStatusCode(new ProjectNotFoundException, ExitCodes.EndpointNotFound)

  describe("Given a Program when running the program and validation of the project fails") {

    val mockValidator = mock[Validator[String]]
    val program = new Program(mockValidator, mock[ControllerFactory])

    Mockito.when(mockValidator.validate(Matchers.anyString()))
      .thenThrow(new InvalidProjectException())

    val args = new Arguments(GenNative.genOneFrom(Operation.values.toSeq),
                             expectedProjectName,
                             expectedCompiler,
                             expectedBuildMode)
    val config = new Configuration("http://localhost")
    val responseCode = program.run(args, config)

    it("Then the response code is as expected") {
      assert(responseCode == ExitCodes.InvalidProject)
    }
  }
}
