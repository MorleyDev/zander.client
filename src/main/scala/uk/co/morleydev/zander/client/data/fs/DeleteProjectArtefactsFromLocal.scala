package uk.co.morleydev.zander.client.data.fs

import java.io.File
import uk.co.morleydev.zander.client.data.DeleteProjectArtefacts

class DeleteProjectArtefactsFromLocal(working : File,
                                      deleteFile : File => Unit,
                                      cleanDirectory : File => Unit) extends DeleteProjectArtefacts {
  override def apply(artefacts : Seq[String]): Unit = {
    artefacts.foreach(f => deleteFile(new File(working, f)))
  }
}
