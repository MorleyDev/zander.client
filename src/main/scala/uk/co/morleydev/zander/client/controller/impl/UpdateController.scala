package uk.co.morleydev.zander.client.controller.impl

import uk.co.morleydev.zander.client.controller.Controller
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.service.{DownloadAcquireUpdateProjectArtefacts, PurgeProjectArtefacts}
import uk.co.morleydev.zander.client.validator.ValidateArtefactDetailsExistence

class UpdateController(validate : ValidateArtefactDetailsExistence,
                       install : DownloadAcquireUpdateProjectArtefacts) extends Controller {
  override def apply(project: Project, compiler: BuildCompiler, mode: BuildMode): Unit = {
    validate(project, compiler, mode)
    install(project, compiler, mode)
  }
}
