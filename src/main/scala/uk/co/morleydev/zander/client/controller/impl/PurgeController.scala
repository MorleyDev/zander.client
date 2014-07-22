package uk.co.morleydev.zander.client.controller.impl

import uk.co.morleydev.zander.client.model.OperationArguments
import uk.co.morleydev.zander.client.service.PurgeProjectArtefacts
import uk.co.morleydev.zander.client.validator.ValidateArtefactDetailsExistence
import uk.co.morleydev.zander.client.controller.Controller

class PurgeController(validateArtefactDetailsExists : ValidateArtefactDetailsExistence,
                      purge : PurgeProjectArtefacts) extends Controller {
  override def apply(args : OperationArguments): Unit = {
    validateArtefactDetailsExists(args.project, args.compiler, args.mode)
    purge(args.project, args.compiler, args.mode)
  }
}
