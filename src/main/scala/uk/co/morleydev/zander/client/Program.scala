package uk.co.morleydev.zander.client

import uk.co.morleydev.zander.client.model.{ExitCodes, Arguments, Configuration}
import uk.co.morleydev.zander.client.validator.Validator
import uk.co.morleydev.zander.client.validator.exceptions.InvalidProjectException
import uk.co.morleydev.zander.client.controller.ControllerFactory
import uk.co.morleydev.zander.client.data.net.exceptions.ProjectNotFoundException

class Program(projectValidator : Validator[String], controllerFactory : ControllerFactory) {
  def run(args : Arguments, config : Configuration) : Int = {

    try {
      projectValidator.validate(args.project)
    } catch {
      case e : InvalidProjectException => return ExitCodes.InvalidProject
    }

    val installController = controllerFactory.createInstallController(config)

    try {
      installController(args.operation, args.project, args.compiler, args.mode)
    } catch {
      case e : ProjectNotFoundException =>
        return ExitCodes.EndpointNotFound
      case e : Exception =>
        println("Unknown error: " )
        e.printStackTrace()
        return ExitCodes.UnknownError
    }
    ExitCodes.Success
  }
}
