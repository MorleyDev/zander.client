package uk.co.morleydev.zander.client.data.fs

import uk.co.morleydev.zander.client.data.WriteProjectSourceDetails
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.store.{CacheDetails, SourceVersion}
import java.io.File
import com.lambdaworks.jacks.JacksMapper

class WriteProjectSourceDetailsToCache(cacheDirectory : File,
                                        writeStringToFile : ((String, File) => Unit)) extends WriteProjectSourceDetails {
  override def apply(p: Project, c: BuildCompiler, m: BuildMode, v: SourceVersion): Unit = {

    val json = JacksMapper.writeValueAsString[CacheDetails](new CacheDetails(v.value))

    writeStringToFile(json, new File(cacheDirectory, ("%s/%s.%s/version.json" +
      "").format(p, c, m)))
  }
}