package uk.co.morleydev.zander.client.controller.impl

import scala.concurrent.ExecutionContext
import uk.co.morleydev.zander.client.controller.Controller
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.service.DownloadAcquireInstallProjectArtefacts
import uk.co.morleydev.zander.client.validator.ValidateArtefactDetailsExistence

class InstallController(validateArtefactsDetailsExist : ValidateArtefactDetailsExistence,
                        artefactAcquire : DownloadAcquireInstallProjectArtefacts,
                        implicit val executionContext : ExecutionContext = ExecutionContext.Implicits.global)
  extends Controller {

  override def apply(project: Project, compiler: BuildCompiler, buildMode: BuildMode): Unit = {

    validateArtefactsDetailsExist(project, compiler, buildMode)
    artefactAcquire(project, compiler, buildMode)
  }
}
