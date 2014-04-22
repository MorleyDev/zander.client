package uk.co.morleydev.zander.client.data.map

import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildCompiler._
import uk.co.morleydev.zander.client.model.arg.BuildMode._
import uk.co.morleydev.zander.client.model.store.ArtefactDetails
import uk.co.morleydev.zander.client.data.ProcessProjectArtefactDetailsMap

object RemoveOverlappingFilesFromArtefactDetails extends ProcessProjectArtefactDetailsMap {
  def apply(details : Map[(Project, BuildCompiler, BuildMode), ArtefactDetails])
    : Map[(Project, BuildCompiler, BuildMode), ArtefactDetails] = {

    val filesToRemove = details
      .flatMap(detail => detail._2.files)
      .groupBy({x => x})
      .filter({case (_, x) => x.size > 1})
      .keys
      .toSeq

    details.map(d => (d._1, new ArtefactDetails(d._2.version, d._2.files.diff(filesToRemove)))).toMap
  }
}
