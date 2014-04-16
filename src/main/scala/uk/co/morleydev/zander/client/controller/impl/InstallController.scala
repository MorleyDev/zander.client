package uk.co.morleydev.zander.client.controller.impl

import uk.co.morleydev.zander.client.controller.Controller
import uk.co.morleydev.zander.client.model.arg.Operation.Operation
import uk.co.morleydev.zander.client.model.arg.Compiler._
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.data.net.GetProject
import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS}

class InstallController(getProjectRemote : GetProject) extends Controller {
  override def apply(operation: Operation, project: String, compiler: Compiler, buildMode: BuildMode): Unit = {
    Await.result(getProjectRemote(project, compiler), Duration(60, SECONDS))
  }
}
