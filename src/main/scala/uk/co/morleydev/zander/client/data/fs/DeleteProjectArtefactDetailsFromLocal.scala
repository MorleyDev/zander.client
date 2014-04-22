package uk.co.morleydev.zander.client.data.fs

import uk.co.morleydev.zander.client.data.DeleteProjectArtefactDetails
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import java.io.File

class DeleteProjectArtefactDetailsFromLocal(workingDirectory : File,
                                            deleteFile : File => Unit) extends DeleteProjectArtefactDetails {
  override def apply(p: Project, c: BuildCompiler, m: BuildMode): Unit = {
    deleteFile(new File(workingDirectory, "%s.%s.%s.json".format(p,c,m)))
  }
}
