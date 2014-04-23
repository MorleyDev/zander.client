package uk.co.morleydev.zander.client.service.impl

import uk.co.morleydev.zander.client.service.AcquireProjectArtefacts
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.store.SourceVersion
import uk.co.morleydev.zander.client.data.{ListProjectCacheFiles, WriteProjectArtefactDetails, InstallProjectArtefact}

class AcquireCachedArtefacts(install : InstallProjectArtefact,
                            listFiles : ListProjectCacheFiles,
                            writeVersion : WriteProjectArtefactDetails) extends AcquireProjectArtefacts {
  override def apply(project: Project, compiler: BuildCompiler, mode: BuildMode, version: SourceVersion) : Unit = {
    install(project, compiler, mode)
    val files = listFiles(project, compiler, mode)
    writeVersion(project, compiler, mode, version, files)
  }
}
