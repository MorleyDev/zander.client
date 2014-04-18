package uk.co.morleydev.zander.client.data

import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.Compiler.Compiler
import scala.concurrent.Future
import uk.co.morleydev.zander.client.model.net.ProjectDto

trait GetProjectDto extends ((Project, Compiler) => Future[ProjectDto]) {
  override def apply(projectName: Project, compiler: Compiler): Future[ProjectDto]
}
