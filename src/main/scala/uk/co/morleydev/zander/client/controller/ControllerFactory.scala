package uk.co.morleydev.zander.client.controller

import uk.co.morleydev.zander.client.controller.impl.InstallController
import uk.co.morleydev.zander.client.data.net.GetProjectRemote
import uk.co.morleydev.zander.client.model.Configuration
import java.net.URL
import uk.co.morleydev.zander.client.data.program.{NativeProcessBuilderFactory, GitDownloadRemote}

trait ControllerFactory {
  def createInstallController(config : Configuration) : Controller
}

class ControllerFactoryImpl(processBuilderFactory : NativeProcessBuilderFactory) extends ControllerFactory {
  def createInstallController(config : Configuration) : Controller = {
    val getProjectRemote = new GetProjectRemote(new URL(config.server))
    val gitDownloadRemote = new GitDownloadRemote(config.programs.git, processBuilderFactory, config.cache)

    new InstallController(getProjectRemote, gitDownloadRemote)
  }
}
