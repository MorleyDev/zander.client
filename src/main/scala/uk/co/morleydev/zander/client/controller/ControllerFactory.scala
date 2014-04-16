package uk.co.morleydev.zander.client.controller

import uk.co.morleydev.zander.client.data.net.GetProjectRemote
import uk.co.morleydev.zander.client.model.Configuration
import java.net.URL
import uk.co.morleydev.zander.client.data.program.GitDownloadRemote
import uk.co.morleydev.zander.client.data.NativeProcessBuilderFactory
import java.io.File

trait ControllerFactory {
  def createInstallController(config : Configuration) : Controller
}

class ControllerFactoryImpl(processBuilderFactory : NativeProcessBuilderFactory) extends ControllerFactory {
  def createInstallController(config : Configuration) : Controller = {
    val getProjectRemote = new GetProjectRemote(new URL(config.server))
    val gitDownloadRemote = new GitDownloadRemote(config.programs.git, processBuilderFactory, new File(config.cache))

    new InstallController(getProjectRemote, gitDownloadRemote)
  }
}
