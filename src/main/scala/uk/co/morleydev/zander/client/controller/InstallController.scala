package uk.co.morleydev.zander.client.controller

import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import scala.concurrent.{Future, Await, future, ExecutionContext}
import scala.concurrent.duration.{Duration, MINUTES}
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.data._
import uk.co.morleydev.zander.client.service.{ProjectArtefactAcquire, ProjectSourceCompile, ProjectSourceAcquire}
import java.io.FileNotFoundException
import uk.co.morleydev.zander.client.controller.exception.LocalArtefactsAlreadyExistException

class InstallController(artefactDetailsReader : ProjectArtefactDetailsReader,
                        getProjectDto : GetProjectDto,
                        sourceAcquire : ProjectSourceAcquire,
                        sourceCompile : ProjectSourceCompile,
                        projectArtefactInstall : ProjectArtefactAcquire,
                        implicit val executionContext : ExecutionContext = ExecutionContext.Implicits.global)
  extends Controller {

  private def checkLocalArtefactsExist(project: Project, compiler: BuildCompiler, buildMode: BuildMode) : Boolean = {
    try {
      artefactDetailsReader(project, compiler, buildMode)
      true
    } catch {
      case e : FileNotFoundException => false
    }
  }

  private def compileAndInstallArtefacts(project: Project, compiler: BuildCompiler, buildMode: BuildMode) : Future[Unit] = {
    getProjectDto(project, compiler)
      .map(dto => sourceAcquire(project, dto))
      .flatMap(version => future({
        sourceCompile(project, compiler, buildMode, version)
    } ).map(_ => projectArtefactInstall(project, compiler, buildMode, version)))
  }

  override def apply(project: Project, compiler: BuildCompiler, buildMode: BuildMode): Unit = {

    val result = future({checkLocalArtefactsExist(project, compiler, buildMode)})
      .flatMap(localArtefactsExist =>
        if (localArtefactsExist)
          throw new LocalArtefactsAlreadyExistException
        else
          compileAndInstallArtefacts(project, compiler, buildMode)
      )

    Await.result(result, Duration(60, MINUTES))
  }
}
