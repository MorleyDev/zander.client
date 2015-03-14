package uk.co.morleydev.zander.client.test.unit

import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.Program
import uk.co.morleydev.zander.client.controller.{Controller, ControllerFactory}
import uk.co.morleydev.zander.client.data.exception.ProjectEndpointNotFoundException
import uk.co.morleydev.zander.client.model.arg.Operation.Operation
import uk.co.morleydev.zander.client.model.{Arguments, Configuration, ExitCodes, OperationArguments}
import uk.co.morleydev.zander.client.test.gen.GenModel
import uk.co.morleydev.zander.client.validator.exception.{LocalArtefactsAlreadyExistException, NoLocalArtefactsExistException}

class ProgramTests extends UnitTest {

  val expectedArguments = new Arguments(
    GenModel.arg.genOperation(),
    new OperationArguments(GenModel.arg.genProject(), GenModel.arg.genCompiler(), GenModel.arg.genBuildMode(), GenModel.arg.genBranch())
  )

  describe("Given a Program When running the program") {

    val mockControllerFactory = mock[ControllerFactory]
    val mockController = mock[Controller]

    Mockito.when(mockControllerFactory.createController(Matchers.any[Operation], Matchers.any[Configuration]()))
           .thenReturn(mockController)

    val program = new Program(mockControllerFactory)

    val config = new Configuration("http://localhost")
    val responseCode = program.run(expectedArguments, config)

    it("Then the install controller is created") {
      Mockito.verify(mockControllerFactory).createController(expectedArguments.operation, config)
    }
    it("Then the install controller is invoked") {
      Mockito.verify(mockController).apply(expectedArguments.operationArgs)
    }
    it("Then the response code is returned") {
      assert(responseCode == ExitCodes.Success)
    }
  }

  def givenProgramWhenRunningControllerThrowsThenExpectedStatusCode(e : Throwable, c : Int) = {
    describe("Given a Program When running the program and the controller throws a " + e.getClass.getSimpleName) {

      val mockControllerFactory = mock[ControllerFactory]
      val mockInstallController = mock[Controller]

      Mockito.when(mockControllerFactory.createController(Matchers.any[Operation], Matchers.any[Configuration]()))
        .thenReturn(mockInstallController)
      Mockito.when(mockInstallController.apply(Matchers.any[OperationArguments]()))
        .thenThrow(e)

      val program = new Program(mockControllerFactory)

      val config = new Configuration("http://localhost")
      val responseCode = program.run(expectedArguments, config)

      it("Then the expected response code is returned") {
        assert(responseCode == c)
      }
    }
  }

  givenProgramWhenRunningControllerThrowsThenExpectedStatusCode(new RuntimeException, ExitCodes.UnknownError)
  givenProgramWhenRunningControllerThrowsThenExpectedStatusCode(new ProjectEndpointNotFoundException, ExitCodes.EndpointNotFound)
  givenProgramWhenRunningControllerThrowsThenExpectedStatusCode(new LocalArtefactsAlreadyExistException, ExitCodes.ArtefactsAlreadyInstalled)
  givenProgramWhenRunningControllerThrowsThenExpectedStatusCode(new NoLocalArtefactsExistException, ExitCodes.ArtefactsNotInstalled)
}
