package uk.co.morleydev.zander.client.service.impl

import uk.co.morleydev.zander.client.service.ProjectArtefactAcquire
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.store.SourceVersion
import uk.co.morleydev.zander.client.data.{ProjectArtefactVersionWriter, ProjectArtefactInstall}

class CachedArtefactAcquire(install : ProjectArtefactInstall,
                            writeVersion : ProjectArtefactVersionWriter) extends ProjectArtefactAcquire {
  override def apply(project: Project, compiler: BuildCompiler, mode: BuildMode, version: SourceVersion) : Unit = {
    install(project, compiler, mode)
    writeVersion(project, compiler, mode, version)
  }
}
