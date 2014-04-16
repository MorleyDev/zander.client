package uk.co.morleydev.zander.client.data.net

import uk.co.morleydev.zander.client.model.arg.Compiler._
import scala.concurrent.Future
import uk.co.morleydev.zander.client.model.net.Project

trait GetProject extends ((String, Compiler) => Future[Project]) {
  override def apply(projectName: String, compiler: Compiler): Future[Project]
}
