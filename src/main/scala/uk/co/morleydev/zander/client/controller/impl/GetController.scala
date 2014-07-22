package uk.co.morleydev.zander.client.controller.impl

import uk.co.morleydev.zander.client.controller.Controller
import uk.co.morleydev.zander.client.data.CheckArtefactDetailsExist
import uk.co.morleydev.zander.client.model.OperationArguments
import uk.co.morleydev.zander.client.service.{DownloadAcquireInstallProjectArtefacts, DownloadAcquireUpdateProjectArtefacts}

class GetController(checkArtefactDetails : CheckArtefactDetailsExist,
                    install : DownloadAcquireInstallProjectArtefacts,
                    update : DownloadAcquireUpdateProjectArtefacts) extends Controller {
  override def apply(args : OperationArguments): Unit = {
    if (checkArtefactDetails(args.project, args.compiler, args.mode))
      update(args.project, args.compiler, args.mode, args.branch)
    else
      install(args.project, args.compiler, args.mode, args.branch)
  }
}
