package uk.co.morleydev.zander.client.controller

import uk.co.morleydev.zander.client.model.arg.Operation.Operation
import uk.co.morleydev.zander.client.model.arg.Compiler._
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{Duration, SECONDS}
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.data._

class InstallController(getProject : GetProject,
                        gitDownload : GitDownload,
                        cmakePrebuild : CMakePrebuild,
                        cmakeBuild : CMakeBuild,
                        cmakeInstall : CMakeInstall,
                        implicit val executionContext : ExecutionContext = ExecutionContext.Implicits.global)
  extends Controller {
  override def apply(project: Project, compiler: Compiler, buildMode: BuildMode): Unit = {
    val result = getProject(project, compiler)
      .map(dto => gitDownload(project, dto))
      .map(dto => cmakePrebuild(project, compiler, buildMode))
      .map(dto => cmakeBuild(project, compiler, buildMode))
      .map(dto => cmakeInstall(project, compiler))
    Await.result(result, Duration(60, SECONDS))
  }
}
