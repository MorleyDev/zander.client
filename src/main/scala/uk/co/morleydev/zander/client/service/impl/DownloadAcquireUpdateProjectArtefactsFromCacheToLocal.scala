package uk.co.morleydev.zander.client.service.impl

import uk.co.morleydev.zander.client.service.{DownloadAcquireInstallProjectArtefacts, PurgeProjectArtefacts, DownloadAcquireUpdateProjectArtefacts}
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode

class DownloadAcquireUpdateProjectArtefactsFromCacheToLocal(purge : PurgeProjectArtefacts,
                                                            install : DownloadAcquireInstallProjectArtefacts)
  extends DownloadAcquireUpdateProjectArtefacts {
  override def apply(p: Project, c: BuildCompiler, m: BuildMode): Unit = {
    purge(p,c,m)
    install(p,c,m)
  }
}
