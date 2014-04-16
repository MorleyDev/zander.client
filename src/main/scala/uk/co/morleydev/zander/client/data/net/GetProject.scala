package uk.co.morleydev.zander.client.data.net

import uk.co.morleydev.zander.client.model.arg.Compiler._
import scala.concurrent.Future
import uk.co.morleydev.zander.client.model.net.ProjectDto
import uk.co.morleydev.zander.client.model.arg.Project

trait GetProject extends ((Project, Compiler) => Future[ProjectDto]) {
  override def apply(projectName: Project, compiler: Compiler): Future[ProjectDto]
}
