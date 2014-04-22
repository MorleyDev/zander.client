package uk.co.morleydev.zander.client.service.impl

import java.io.File
import uk.co.morleydev.zander.client.data.ReadProjectArtefactDetails
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.store.ArtefactDetails
import uk.co.morleydev.zander.client.service.GetAllProjectArtefactDetails

class GetAllProjectArtefactDetailsFromLocal(workingDirectory : File,
                                            listFilesInDirectory : (File, String) => Seq[File],
                                            splitFileNameToProjectDetails : (String => (Project, BuildCompiler, BuildMode)),
                                            readArtefactDetails : ReadProjectArtefactDetails) extends GetAllProjectArtefactDetails {


  override def apply(): Map[(Project, BuildCompiler, BuildMode), ArtefactDetails] = {

    val files = listFilesInDirectory(workingDirectory, "json").filter(s => s.getName.count(_ == '.') >= 3)

    files.map(f => try {
      splitFileNameToProjectDetails(f.getName)
    } catch {
      case e : NoSuchElementException => null
      case e : IllegalArgumentException => null
    }).filter(_ != null)
      .map(f => (f, readArtefactDetails(f._1, f._2, f._3)))
      .toMap
  }
}
