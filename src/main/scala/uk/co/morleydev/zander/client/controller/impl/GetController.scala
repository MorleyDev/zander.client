package uk.co.morleydev.zander.client.controller.impl

import uk.co.morleydev.zander.client.controller.Controller
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.data.CheckArtefactDetailsExist
import uk.co.morleydev.zander.client.service.{DownloadAcquireUpdateProjectArtefacts, DownloadAcquireInstallProjectArtefacts}

class GetController(checkArtefactDetails : CheckArtefactDetailsExist,
                    install : DownloadAcquireInstallProjectArtefacts,
                    update : DownloadAcquireUpdateProjectArtefacts) extends Controller {
  override def apply(project: Project, compiler: BuildCompiler, buildMode: BuildMode): Unit = {
    if (checkArtefactDetails(project, compiler, buildMode))
      update(project,compiler,buildMode)
    else
      install(project,compiler,buildMode)
  }
}
