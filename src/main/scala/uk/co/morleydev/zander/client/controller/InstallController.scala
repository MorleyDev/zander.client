package uk.co.morleydev.zander.client.controller

import uk.co.morleydev.zander.client.model.arg.Compiler.Compiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{Duration, SECONDS}
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.data._

class InstallController(getProject : GetProjectDto,
                        sourceDownload : ProjectSourceDownload,
                        sourcePrebuild : ProjectSourcePrebuild,
                        sourceBuild : ProjectSourceBuild,
                        sourceInstall : ProjectSourceInstall,
                        projectArtefactInstall : ProjectArtefactInstall,
                        implicit val executionContext : ExecutionContext = ExecutionContext.Implicits.global)
  extends Controller {
  override def apply(project: Project, compiler: Compiler, buildMode: BuildMode): Unit = {
    val result = getProject(project, compiler)
      .map(dto => sourceDownload(project, dto))
      .map(_ => sourcePrebuild(project, compiler, buildMode))
      .map(_ => sourceBuild(project, compiler, buildMode))
      .map(_ => sourceInstall(project, compiler))
      .map(_ => projectArtefactInstall(project, compiler, buildMode))
    Await.result(result, Duration(60, SECONDS))
  }
}
