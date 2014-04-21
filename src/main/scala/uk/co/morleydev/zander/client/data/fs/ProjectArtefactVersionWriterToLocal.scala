package uk.co.morleydev.zander.client.data.fs

import uk.co.morleydev.zander.client.data.{ProjectSourceListFiles, ProjectArtefactVersionWriter}
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import java.io.File
import com.lambdaworks.jacks.JacksMapper
import uk.co.morleydev.zander.client.model.store.{SourceVersion, ArtefactDetails}

class ProjectArtefactVersionWriterToLocal(workingDirectory : File, writeStringToFile : ((String, File) => Unit))
  extends ProjectArtefactVersionWriter {
  override def apply(p : Project, c : BuildCompiler, b : BuildMode, v : SourceVersion, files: Seq[String]) : Unit = {

    val versionJson = JacksMapper.writeValueAsString[ArtefactDetails](new ArtefactDetails(v.value, files))

    writeStringToFile(versionJson, new File(workingDirectory, "%s.%s.%s.json".format(p,c,b)))
  }
}
