package uk.co.morleydev.zander.client.service.impl

import uk.co.morleydev.zander.client.data.GetProjectDto
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.service.{AcquireProjectArtefacts, CompileProjectSource, AcquireProjectSource, DownloadAcquireInstallProjectArtefacts}
import scala.concurrent.{ExecutionContext, Await}
import scala.concurrent.duration.Duration
import scala.concurrent.duration.MINUTES

class DownloadAcquireInstallProjectArtefactsFromCacheToLocal(getProjectDto : GetProjectDto,
                                                       sourceAcquire : AcquireProjectSource,
                                                       sourceCompile : CompileProjectSource,
                                                       projectArtefactInstall : AcquireProjectArtefacts,
                                                       implicit val executor: ExecutionContext = ExecutionContext.Implicits.global)
  extends DownloadAcquireInstallProjectArtefacts {
  override def apply(project: Project, compiler: BuildCompiler, mode: BuildMode): Unit = {
    val result = getProjectDto(project, compiler).map(dto => {
      val version = sourceAcquire(project, dto)
      sourceCompile(project, compiler, mode, version)
      projectArtefactInstall(project, compiler, mode, version)
    })
    Await.result(result, Duration(60, MINUTES))
  }
}
