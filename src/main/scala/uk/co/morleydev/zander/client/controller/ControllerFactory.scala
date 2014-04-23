package uk.co.morleydev.zander.client.controller

import java.io.File
import java.net.URL
import uk.co.morleydev.zander.client.data.fs.DeleteProjectArtefactsFromLocal
import uk.co.morleydev.zander.client.data.net.GetProjectDtoRemote
import uk.co.morleydev.zander.client.data.{DataFactoryImpl, NativeProcessBuilderFactory}
import uk.co.morleydev.zander.client.model.Configuration
import uk.co.morleydev.zander.client.model.arg.Operation
import uk.co.morleydev.zander.client.model.arg.Operation.Operation
import uk.co.morleydev.zander.client.service.ServiceFactoryImpl
import uk.co.morleydev.zander.client.validator.ValidatorFactoryImpl

trait ControllerFactory {
  def createController(operation : Operation, config : Configuration) : Controller
}

class ControllerFactoryImpl(processBuilderFactory : NativeProcessBuilderFactory,
                            temporaryDirectory : File,
                            workingDirectory : File) extends ControllerFactory {

  private val serviceFactory = new ServiceFactoryImpl(processBuilderFactory, temporaryDirectory, workingDirectory)
  private val validatorFactory = new ValidatorFactoryImpl(processBuilderFactory, temporaryDirectory, workingDirectory)

  private val configFactoryMap = Map[Operation, (Configuration => Controller)](
    Operation.Install -> createInstallController,
    Operation.Purge -> createPurgeController,
    Operation.Update -> createUpdateController
  )

  def createController(operation : Operation, config : Configuration) : Controller = configFactoryMap(operation)(config)

  private def createInstallController(config : Configuration) : Controller = {
    val validateArtefactDetailsDoNotExist = validatorFactory.createValidateArtefactDetailsDoNotExist()
    val downloadCompileAcquire = serviceFactory.createDownloadAcquireInstallProjectArtefactsFromCacheToLocal(config)

    new InstallController(validateArtefactDetailsDoNotExist, downloadCompileAcquire)
  }

  private def createPurgeController(config : Configuration) : Controller = {
    val validateArtefactDetailsExist = validatorFactory.createValidateArtefactDetailsExist()
    val purgeFromLocal = serviceFactory.createArtefactPurgeFromLocal()

    new PurgeController(validateArtefactDetailsExist, purgeFromLocal)
  }

  private def createUpdateController(config : Configuration) : Controller = {
    val validateArtefactDetailsExist = validatorFactory.createValidateArtefactDetailsExist()
    val purgeFromLocal = serviceFactory.createArtefactPurgeFromLocal()
    val downloadCompileAcquire = serviceFactory.createDownloadAcquireInstallProjectArtefactsFromCacheToLocal(config)

    new UpdateController(validateArtefactDetailsExist, purgeFromLocal, downloadCompileAcquire)
  }
}
