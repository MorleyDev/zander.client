package uk.co.morleydev.zander.client.service.impl

import uk.co.morleydev.zander.client.service.PurgeProjectArtefacts
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.data.{DeleteProjectArtefacts, DeleteProjectArtefactDetails, ReadProjectArtefactDetails}
import java.io.FileNotFoundException
import uk.co.morleydev.zander.client.service.exception.NoLocalArtefactsExistException

class LocalArtefactPurge(getDetails : ReadProjectArtefactDetails,
                         deleteDetails : DeleteProjectArtefactDetails,
                         deleteArtefacts : DeleteProjectArtefacts) extends PurgeProjectArtefacts {
  override def apply(p: Project, c: BuildCompiler, m: BuildMode): Unit = {
    val details = try {
      getDetails(p, c, m)
    } catch {
      case e: FileNotFoundException =>
        throw new NoLocalArtefactsExistException()
    }
    deleteDetails(p, c, m)
    deleteArtefacts(details)
  }
}
