package uk.co.morleydev.zander.client.test.unit

import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.Program
import uk.co.morleydev.zander.client.controller.exception.LocalArtefactsAlreadyExistException
import uk.co.morleydev.zander.client.controller.{Controller, ControllerFactory}
import uk.co.morleydev.zander.client.data.exception.ProjectEndpointNotFoundException
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.Operation.Operation
import uk.co.morleydev.zander.client.model.arg.{Project, BuildCompiler, BuildMode}
import uk.co.morleydev.zander.client.model.{ExitCodes, Arguments, Configuration}
import uk.co.morleydev.zander.client.service.exception.NoLocalArtefactsExistException
import uk.co.morleydev.zander.client.test.gen.{GenModel, GenNative}

class ProgramTests extends UnitTest {

  val expectedOperation = GenModel.arg.genOperation()
  val expectedCompiler = GenNative.genOneFrom(BuildCompiler.values.toSeq)
  val expectedBuildMode = GenNative.genOneFrom(BuildMode.values.toSeq)
  val expectedProjectName = new Project(GenNative.genAlphaNumericString(1, 20))
  
  describe("Given a Program When running the program") {

    val mockControllerFactory = mock[ControllerFactory]
    val mockController = mock[Controller]

    Mockito.when(mockControllerFactory.createController(Matchers.any[Operation], Matchers.any[Configuration]()))
           .thenReturn(mockController)

    val program = new Program(mockControllerFactory)

    val args = new Arguments(expectedOperation, expectedProjectName, expectedCompiler, expectedBuildMode)
    val config = new Configuration("http://localhost")
    val responseCode = program.run(args, config)

    it("Then the install controller is created") {
      Mockito.verify(mockControllerFactory).createController(expectedOperation, config)
    }
    it("Then the install controller is invoked") {
      Mockito.verify(mockController).apply(expectedProjectName, expectedCompiler, expectedBuildMode)
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
      Mockito.when(mockInstallController.apply(Matchers.any[Project](), Matchers.any[BuildCompiler](), Matchers.any[BuildMode]()))
        .thenThrow(e)

      val program = new Program(mockControllerFactory)

      val args = new Arguments(expectedOperation, expectedProjectName, expectedCompiler, expectedBuildMode)
      val config = new Configuration("http://localhost")
      val responseCode = program.run(args, config)

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
