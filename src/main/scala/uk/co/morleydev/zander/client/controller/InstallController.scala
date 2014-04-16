package uk.co.morleydev.zander.client.controller

import uk.co.morleydev.zander.client.model.arg.Operation.Operation
import uk.co.morleydev.zander.client.model.arg.Compiler._
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{Duration, SECONDS}
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.data.{GetProject, GitDownload}

class InstallController(getProject : GetProject,
                        gitDownload : GitDownload,
                        implicit val executionContext : ExecutionContext = ExecutionContext.Implicits.global)
  extends Controller {
  override def apply(operation: Operation, project: Project, compiler: Compiler, buildMode: BuildMode): Unit = {
    val result = getProject(project, compiler)
      .flatMap(dto => gitDownload(project, dto))
    Await.result(result, Duration(60, SECONDS))
  }
}
