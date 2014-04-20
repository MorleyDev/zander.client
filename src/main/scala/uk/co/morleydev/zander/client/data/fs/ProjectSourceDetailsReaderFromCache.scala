package uk.co.morleydev.zander.client.data.fs

import uk.co.morleydev.zander.client.data.ProjectSourceDetailsReader
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.store.SourceDetails
import java.io.File
import com.lambdaworks.jacks.JacksMapper

class ProjectSourceDetailsReaderFromCache(cache : File, readFileToString : (File => String))
  extends ProjectSourceDetailsReader {

  override def apply(project : Project, compiler : BuildCompiler, mode : BuildMode) : SourceDetails = {
    val json = readFileToString(new File(cache, "/%s/%s.%s/version.json".format(project, compiler, mode)))

    JacksMapper.readValue[SourceDetails](json)
  }
}
