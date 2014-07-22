package uk.co.morleydev.zander.client.controller.impl

import uk.co.morleydev.zander.client.controller.Controller
import uk.co.morleydev.zander.client.model.OperationArguments
import uk.co.morleydev.zander.client.service.DownloadAcquireUpdateProjectArtefacts
import uk.co.morleydev.zander.client.validator.ValidateArtefactDetailsExistence

class UpdateController(validate : ValidateArtefactDetailsExistence,
                       install : DownloadAcquireUpdateProjectArtefacts) extends Controller {
  override def apply(args : OperationArguments): Unit = {
    validate(args.project, args.compiler, args.mode)
    install(args.project, args.compiler, args.mode, args.branch)
  }
}
