package uk.co.morleydev.zander.client.service.impl

import uk.co.morleydev.zander.client.data.{ProcessProjectArtefactDetailsMap, DeleteProjectArtefacts, DeleteProjectArtefactDetails}
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.service.exception.NoLocalArtefactsExistException
import uk.co.morleydev.zander.client.service.{GetAllProjectArtefactDetails, PurgeProjectArtefacts}
import uk.co.morleydev.zander.client.util.Log

class LocalArtefactPurge(getDetails : GetAllProjectArtefactDetails,
                         removeOverlappingFiles : ProcessProjectArtefactDetailsMap,
                         deleteDetails : DeleteProjectArtefactDetails,
                         deleteArtefacts : DeleteProjectArtefacts) extends PurgeProjectArtefacts {
  override def apply(p: Project, c: BuildCompiler, m: BuildMode): Unit = {

    Log.message("Purging artefacts for %s %s %s".format(p,c,m))

    val allDetails = getDetails()
    val processedDetails = removeOverlappingFiles(allDetails)
    val details = processedDetails.get((p,c,m)) match {
      case Some(f) => f
      case None =>
        Log.error("Artefacts not installed")
        throw new NoLocalArtefactsExistException()
    }
    deleteDetails(p, c, m)
    deleteArtefacts(details.files)
  }
}
