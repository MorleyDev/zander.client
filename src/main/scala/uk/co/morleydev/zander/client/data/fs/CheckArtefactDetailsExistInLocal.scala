package uk.co.morleydev.zander.client.data.fs

import uk.co.morleydev.zander.client.data.{ReadProjectArtefactDetails, CheckArtefactDetailsExist}
import java.io.{FileNotFoundException, File}
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode

class CheckArtefactDetailsExistInLocal(workingDirectory : File,
                                       readArtefactDetails : ReadProjectArtefactDetails) extends CheckArtefactDetailsExist {
  override def apply(project: Project, compiler: BuildCompiler, mode: BuildMode): Boolean = try {
    readArtefactDetails(project, compiler, mode)
    true
  } catch {
    case e : FileNotFoundException => false
  }
}
