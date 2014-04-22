package uk.co.morleydev.zander.client.data.fs

import uk.co.morleydev.zander.client.data.DeleteProjectArtefacts
import uk.co.morleydev.zander.client.model.store.ArtefactDetails
import java.io.File

class DeleteProjectArtefactsFromLocal(working : File,
                                      deleteFile : File => Unit,
                                      cleanDirectory : File => Unit) extends DeleteProjectArtefacts {
  override def apply(artefactDetails : ArtefactDetails): Unit = {
    artefactDetails.files.foreach(f => deleteFile(new File(working, f)))
  }
}
