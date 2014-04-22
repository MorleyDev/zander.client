package uk.co.morleydev.zander.client.data.fs

import uk.co.morleydev.zander.client.data.ReadProjectArtefactDetails
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.store.ArtefactDetails
import java.io.File
import com.lambdaworks.jacks.JacksMapper

class ReadProjectArtefactDetailsFromLocal(workingDirectory : File,
                                           fileToStringReader : (File => String)) extends ReadProjectArtefactDetails {
  override def apply(p: Project, c: BuildCompiler, m: BuildMode): ArtefactDetails = {

    val file = new File(workingDirectory, "%s.%s.%s.json".format(p,c,m))
    val json = fileToStringReader(file)

    JacksMapper.readValue[ArtefactDetails](json)
  }
}
