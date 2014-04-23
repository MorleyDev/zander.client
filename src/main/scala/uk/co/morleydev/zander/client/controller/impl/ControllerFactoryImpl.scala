package uk.co.morleydev.zander.client.controller.impl

import java.io.File
import uk.co.morleydev.zander.client.controller._
import uk.co.morleydev.zander.client.data.NativeProcessBuilderFactory
import uk.co.morleydev.zander.client.model.Configuration
import uk.co.morleydev.zander.client.model.arg.Operation
import uk.co.morleydev.zander.client.model.arg.Operation.Operation
import uk.co.morleydev.zander.client.service.impl.ServiceFactoryImpl
import uk.co.morleydev.zander.client.validator.impl.ValidatorFactoryImpl
import uk.co.morleydev.zander.client.data.impl.DataFactoryImpl

class ControllerFactoryImpl(processBuilderFactory : NativeProcessBuilderFactory,
                            temporaryDirectory : File,
                            workingDirectory : File) extends ControllerFactory {

  private val dataFactory = new DataFactoryImpl(processBuilderFactory, temporaryDirectory, workingDirectory)
  private val serviceFactory = new ServiceFactoryImpl(processBuilderFactory, temporaryDirectory, workingDirectory)
  private val validatorFactory = new ValidatorFactoryImpl(processBuilderFactory, temporaryDirectory, workingDirectory)

  private val configFactoryMap = Map[Operation, (Configuration => Controller)](
    Operation.Install -> createInstallController,
    Operation.Purge -> createPurgeController,
    Operation.Update -> createUpdateController,
    Operation.Get -> createGetController
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
    val downloadCompileAcquire = serviceFactory.createDownloadAcquireUpdateProjectArtefactsFromCacheToLocal(config)

    new UpdateController(validateArtefactDetailsExist, downloadCompileAcquire)
  }

  private def createGetController(config : Configuration) : Controller = {
    val checkArtefactsExist = dataFactory.createCheckArtefactDetailsExist()
    val downloadUpdateAcquire = serviceFactory.createDownloadAcquireUpdateProjectArtefactsFromCacheToLocal(config)
    val downloadCompileAcquire = serviceFactory.createDownloadAcquireInstallProjectArtefactsFromCacheToLocal(config)

    new GetController(checkArtefactsExist, downloadCompileAcquire, downloadUpdateAcquire)
  }
}
