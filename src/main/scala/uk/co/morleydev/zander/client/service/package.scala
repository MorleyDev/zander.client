package uk.co.morleydev.zander.client

import uk.co.morleydev.zander.client.model.arg.BuildCompiler._
import uk.co.morleydev.zander.client.model.arg.BuildMode._
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.net.ProjectDto
import uk.co.morleydev.zander.client.model.store.{SourceVersion, ArtefactDetails}

package object service {

  type GetAllProjectArtefactDetails = (() => Map[(Project, BuildCompiler, BuildMode), ArtefactDetails])

  type PurgeProjectArtefacts = ((Project, BuildCompiler, BuildMode) => Unit)

  type DownloadAcquireInstallProjectArtefacts = ((Project, BuildCompiler, BuildMode) => Unit)

  type DownloadAcquireUpdateProjectArtefacts = ((Project, BuildCompiler, BuildMode) => Unit)

  type CompileProjectSource = ((Project, BuildCompiler, BuildMode, SourceVersion) => Unit)

  type AcquireProjectSource = ((Project, ProjectDto) => SourceVersion)

  type AcquireProjectArtefacts = ((Project, BuildCompiler, BuildMode, SourceVersion) => Unit)
}
