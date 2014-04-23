package uk.co.morleydev.zander.client.controller

import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.validator.ValidateArtefactDetailsExistence
import uk.co.morleydev.zander.client.service.{PurgeProjectArtefacts, DownloadAcquireInstallProjectArtefacts}

class UpdateController(validate : ValidateArtefactDetailsExistence,
                       purge : PurgeProjectArtefacts,
                       install : DownloadAcquireInstallProjectArtefacts) extends Controller {
  override def apply(project: Project, compiler: BuildCompiler, mode: BuildMode): Unit = {
    validate(project, compiler, mode)
    purge(project, compiler, mode)
    install(project, compiler, mode)
  }
}
