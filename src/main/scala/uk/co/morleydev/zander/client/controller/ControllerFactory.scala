package uk.co.morleydev.zander.client.controller

import uk.co.morleydev.zander.client.data.net.GetProjectRemote
import uk.co.morleydev.zander.client.model.Configuration
import java.net.URL
import uk.co.morleydev.zander.client.data.program.{CMakePrebuildLocal, LocalProgramRunner, GitDownloadRemote}
import uk.co.morleydev.zander.client.data.NativeProcessBuilderFactory
import java.io.File

trait ControllerFactory {
  def createInstallController(config : Configuration) : Controller
}

class ControllerFactoryImpl(processBuilderFactory : NativeProcessBuilderFactory) extends ControllerFactory {
  def createInstallController(config : Configuration) : Controller = {

    val getProjectRemote = new GetProjectRemote(new URL(config.server))

    val programRunner = new LocalProgramRunner(processBuilderFactory)
    val gitDownloadRemote = new GitDownloadRemote(config.programs.git,
      programRunner,
      new File(config.cache))

    val cmakePrebuild = new CMakePrebuildLocal(config.programs.cmake,
      programRunner,
      new File(config.cache),
      new File(config.cache, "tmp"))

    new InstallController(getProjectRemote, gitDownloadRemote, cmakePrebuild)
  }
}
