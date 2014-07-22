package uk.co.morleydev.zander.client.controller.impl

import uk.co.morleydev.zander.client.controller.Controller
import uk.co.morleydev.zander.client.model.OperationArguments
import uk.co.morleydev.zander.client.service.DownloadAcquireInstallProjectArtefacts
import uk.co.morleydev.zander.client.validator.ValidateArtefactDetailsExistence

import scala.concurrent.ExecutionContext

class InstallController(validateArtefactsDetailsExist : ValidateArtefactDetailsExistence,
                        artefactAcquire : DownloadAcquireInstallProjectArtefacts,
                        implicit val executionContext : ExecutionContext = ExecutionContext.Implicits.global)
  extends Controller {

  override def apply(args : OperationArguments): Unit = {

    validateArtefactsDetailsExist(args.project, args.compiler, args.mode)
    artefactAcquire(args.project, args.compiler, args.mode, args.branch)
  }
}
