package uk.co.morleydev.zander.client

import uk.co.morleydev.zander.client.model.{ExitCodes, Arguments, Configuration}
import uk.co.morleydev.zander.client.controller.ControllerFactory
import uk.co.morleydev.zander.client.data.exception.ProjectEndpointNotFoundException
import uk.co.morleydev.zander.client.util.Log
import uk.co.morleydev.zander.client.validator.exception.{NoLocalArtefactsExistException, LocalArtefactsAlreadyExistException}

class Program(controllerFactory : ControllerFactory) {
  def run(args : Arguments, config : Configuration) : Int = {

    val installController = controllerFactory.createController(args.operation, config)

    try {
      installController(args.project, args.compiler, args.mode)
      ExitCodes.Success
    } catch {
      case e : LocalArtefactsAlreadyExistException => ExitCodes.ArtefactsAlreadyInstalled
      case e : NoLocalArtefactsExistException => ExitCodes.ArtefactsNotInstalled
      case e : ProjectEndpointNotFoundException => ExitCodes.EndpointNotFound
      case e : Exception =>
        Log.error("Unexpected Error Occurred" + e.getStackTrace.mkString("\n"))
        ExitCodes.UnknownError
    }
  }
}
