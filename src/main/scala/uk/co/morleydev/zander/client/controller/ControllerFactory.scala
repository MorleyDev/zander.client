package uk.co.morleydev.zander.client.controller

import uk.co.morleydev.zander.client.data.net.GetProjectDtoRemote
import java.net.URL
import uk.co.morleydev.zander.client.data.NativeProcessBuilderFactory
import java.io.File
import uk.co.morleydev.zander.client.model.Configuration
import uk.co.morleydev.zander.client.data.fs.ProjectArtefactDetailsReaderFromCache
import uk.co.morleydev.zander.client.util.Using.using
import uk.co.morleydev.zander.client.service.ServiceFactory
import scala.io.Source

trait ControllerFactory {
  def createInstallController(config : Configuration) : Controller
}

class ControllerFactoryImpl(processBuilderFactory : NativeProcessBuilderFactory,
                            temporaryDirectory : File,
                            workingDirectory : File) extends ControllerFactory {

  private val serviceFactory = new ServiceFactory(processBuilderFactory, temporaryDirectory, workingDirectory)

  def createInstallController(config : Configuration) : Controller = {

    val getProjectRemote = new GetProjectDtoRemote(new URL(config.server))

    val artefactDetailsReader = new ProjectArtefactDetailsReaderFromCache(workingDirectory, f => using(Source.fromFile(f)) {
      s => s.getLines().mkString("\n")
    })
    val sourceAcquireService = serviceFactory.createGitSourceAcquire(config)
    val sourceCompileService = serviceFactory.createCMakeProjectSourceCompile(config)
    val artefactAcquire = serviceFactory.createCachedArtefactAcquire(config)

    new InstallController(artefactDetailsReader,
      getProjectRemote,
      sourceAcquireService,
      sourceCompileService,
      artefactAcquire)
  }
}
