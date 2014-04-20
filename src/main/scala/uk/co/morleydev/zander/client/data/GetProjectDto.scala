package uk.co.morleydev.zander.client.data

import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import scala.concurrent.Future
import uk.co.morleydev.zander.client.model.net.ProjectDto

trait GetProjectDto extends ((Project, BuildCompiler) => Future[ProjectDto]) {
  override def apply(projectName: Project, compiler: BuildCompiler): Future[ProjectDto]
}
