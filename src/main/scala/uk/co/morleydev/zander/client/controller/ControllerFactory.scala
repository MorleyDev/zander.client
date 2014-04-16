package uk.co.morleydev.zander.client.controller

import uk.co.morleydev.zander.client.controller.impl.InstallController
import uk.co.morleydev.zander.client.data.net.GetProjectRemote
import uk.co.morleydev.zander.client.model.Configuration
import java.net.URL

trait ControllerFactory {
  def createInstallController(config : Configuration) : Controller
}

object ControllerFactoryImpl extends ControllerFactory {
  def createInstallController(config : Configuration) : Controller = new InstallController(new GetProjectRemote(new URL(config.server)))
}
