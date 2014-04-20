package uk.co.morleydev.zander.client.data.fs

import uk.co.morleydev.zander.client.data.ProjectSourceDetailsWriter
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.store.{SourceDetails, SourceVersion}
import java.io.File
import com.lambdaworks.jacks.JacksMapper

class ProjectSourceDetailsWriterToCache(cacheDirectory : File,
                                        writeStringToFile : ((String, File) => Unit)) extends ProjectSourceDetailsWriter {
  override def apply(p: Project, c: BuildCompiler, m: BuildMode, v: SourceVersion): Unit = {

    val json = JacksMapper.writeValueAsString[SourceDetails](new SourceDetails(v.value))

    writeStringToFile(json, new File(cacheDirectory, ("%s/%s.%s/version.json" +
      "").format(p, c, m)))
  }
}
