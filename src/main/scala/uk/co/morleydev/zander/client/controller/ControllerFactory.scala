package uk.co.morleydev.zander.client.controller

import uk.co.morleydev.zander.client.data.net.GetProjectDtoRemote
import uk.co.morleydev.zander.client.model.Configuration
import java.net.URL
import uk.co.morleydev.zander.client.data.program._
import uk.co.morleydev.zander.client.data.NativeProcessBuilderFactory
import java.io.File
import uk.co.morleydev.zander.client.model.Configuration
import uk.co.morleydev.zander.client.data.fs.ProjectArtefactInstallFromCache
import org.apache.commons.io.FileUtils
import uk.co.morleydev.zander.client.util.Log
import uk.co.morleydev.zander.client.service.impl.{CachedSourceCompile, CachedSourceAcquire}
import uk.co.morleydev.zander.client.service.ServiceFactory

trait ControllerFactory {
  def createInstallController(config : Configuration) : Controller
}

class ControllerFactoryImpl(processBuilderFactory : NativeProcessBuilderFactory,
                            temporaryDirectory : File,
                            workingDirectory : File) extends ControllerFactory {

  private val serviceFactory = new ServiceFactory(processBuilderFactory, temporaryDirectory, workingDirectory)

  def createInstallController(config : Configuration) : Controller = {

    val getProjectRemote = new GetProjectDtoRemote(new URL(config.server))

    val sourceAcquireService = serviceFactory.createGitSourceAcquire(config)
    val sourceCompileService = serviceFactory.createCMakeProjectSourceCompile(config)
    val artefactAcquire = serviceFactory.createCachedArtefactAcquire(config)

    new InstallController(getProjectRemote, sourceAcquireService, sourceCompileService, artefactAcquire)
  }
}
