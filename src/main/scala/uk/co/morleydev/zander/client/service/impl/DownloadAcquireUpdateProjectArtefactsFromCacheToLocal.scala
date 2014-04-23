package uk.co.morleydev.zander.client.service.impl

import uk.co.morleydev.zander.client.service._
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.data.{ReadProjectArtefactDetails, GetProjectDto}
import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._

class DownloadAcquireUpdateProjectArtefactsFromCacheToLocal(getProjectDto : GetProjectDto,
                                                            sourceAcquire : AcquireProjectSource,
                                                            sourceCompile : CompileProjectSource,
                                                            readArtefactDetails : ReadProjectArtefactDetails,
                                                            artefactPurge : PurgeProjectArtefacts,
                                                            projectArtefactInstall : AcquireProjectArtefacts,
                                                            implicit val executor: ExecutionContext = ExecutionContext.Implicits.global)
  extends DownloadAcquireUpdateProjectArtefacts {
  override def apply(p: Project, c: BuildCompiler, m: BuildMode): Unit = {
    val result = getProjectDto(p,c)
      .map(dto => {
        val sourceVersion = sourceAcquire(p, dto)
        sourceCompile(p,c,m, sourceVersion)
        val artefactDetails = readArtefactDetails(p,c,m)

        if (artefactDetails.version != sourceVersion.value) {
          artefactPurge(p, c, m)
          projectArtefactInstall(p, c, m, sourceVersion)
        }
    })
    Await.result(result, Duration(60, MINUTES))
  }
}
