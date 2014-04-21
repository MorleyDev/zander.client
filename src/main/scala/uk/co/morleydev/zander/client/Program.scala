package uk.co.morleydev.zander.client

import uk.co.morleydev.zander.client.model.{ExitCodes, Arguments, Configuration}
import uk.co.morleydev.zander.client.controller.ControllerFactory
import uk.co.morleydev.zander.client.data.exception.ProjectNotFoundException
import uk.co.morleydev.zander.client.controller.exception.LocalArtefactsAlreadyExistException
import uk.co.morleydev.zander.client.util.Log

class Program(controllerFactory : ControllerFactory) {
  def run(args : Arguments, config : Configuration) : Int = {

    val installController = controllerFactory.createInstallController(config)

    try {
      installController(args.project, args.compiler, args.mode)
      ExitCodes.Success
    } catch {
      case e : LocalArtefactsAlreadyExistException => ExitCodes.ArtefactsAlreadyInstalled
      case e : ProjectNotFoundException => ExitCodes.EndpointNotFound
      case e : Exception =>
        Log("Unexpected Error Occurred" )
        e.printStackTrace()
        ExitCodes.UnknownError
    }
  }
}
