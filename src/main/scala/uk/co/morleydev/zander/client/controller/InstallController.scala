package uk.co.morleydev.zander.client.controller

import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{Duration, MINUTES}
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.data._
import uk.co.morleydev.zander.client.service.{ProjectArtefactAcquire, ProjectSourceCompile, ProjectSourceAcquire}

class InstallController(getProjectDto : GetProjectDto,
                        sourceAcquire : ProjectSourceAcquire,
                        sourceCompile : ProjectSourceCompile,
                        projectArtefactInstall : ProjectArtefactAcquire,
                        implicit val executionContext : ExecutionContext = ExecutionContext.Implicits.global)
  extends Controller {
  override def apply(project: Project, compiler: BuildCompiler, buildMode: BuildMode): Unit = {
    val result = getProjectDto(project, compiler)
      .map(dto => sourceAcquire(project, dto))
      .map(version => { sourceCompile(project, compiler, buildMode, version); version })
      .map(version => projectArtefactInstall(project, compiler, buildMode, version))
    Await.result(result, Duration(60, MINUTES))
  }
}
