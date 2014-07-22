package uk.co.morleydev.zander.client

import uk.co.morleydev.zander.client.model.arg.BuildCompiler._
import uk.co.morleydev.zander.client.model.arg.BuildMode._
import uk.co.morleydev.zander.client.model.arg.{Branch, Project}
import uk.co.morleydev.zander.client.model.net.ProjectDto
import uk.co.morleydev.zander.client.model.store.{SourceVersion, ArtefactDetails}

package object service {

  type GetAllProjectArtefactDetails = (() => Map[(Project, BuildCompiler, BuildMode), ArtefactDetails])

  type PurgeProjectArtefacts = ((Project, BuildCompiler, BuildMode) => Unit)

  type DownloadAcquireInstallProjectArtefacts = ((Project, BuildCompiler, BuildMode, Branch) => Unit)

  type DownloadAcquireUpdateProjectArtefacts = ((Project, BuildCompiler, BuildMode, Branch) => Unit)

  type CompileProjectSource = ((Project, BuildCompiler, BuildMode, Branch, SourceVersion) => Unit)

  type AcquireProjectSource = ((Project, ProjectDto, Branch) => SourceVersion)

  type AcquireProjectArtefacts = ((Project, BuildCompiler, BuildMode, Branch, SourceVersion) => Unit)
}
