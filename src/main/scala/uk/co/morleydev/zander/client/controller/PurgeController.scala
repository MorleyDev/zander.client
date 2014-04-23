package uk.co.morleydev.zander.client.controller

import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.service.PurgeProjectArtefacts
import uk.co.morleydev.zander.client.validator.ValidateArtefactDetailsExistence

class PurgeController(validateArtefactDetailsExists : ValidateArtefactDetailsExistence,
                      purge : PurgeProjectArtefacts) extends Controller {
  override def apply(project: Project, compiler: BuildCompiler, buildMode: BuildMode): Unit = {
    validateArtefactDetailsExists(project, compiler, buildMode)
    purge(project, compiler, buildMode)
  }
}
