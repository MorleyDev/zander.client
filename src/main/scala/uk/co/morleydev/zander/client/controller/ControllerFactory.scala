package uk.co.morleydev.zander.client.controller

import uk.co.morleydev.zander.client.model.Configuration
import uk.co.morleydev.zander.client.model.arg.Operation.Operation

trait ControllerFactory {
  def createController(operation : Operation, config : Configuration) : Controller
}


