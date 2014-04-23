package uk.co.morleydev.zander.client.controller

import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.service.exception.NoLocalArtefactsExistException

class UpdateController extends Controller {
  override def apply(project: Project, compiler: BuildCompiler, buildMode: BuildMode): Unit = {
    throw new NoLocalArtefactsExistException()
  }
}
