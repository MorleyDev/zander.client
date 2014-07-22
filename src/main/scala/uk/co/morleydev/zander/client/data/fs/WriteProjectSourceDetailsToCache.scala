package uk.co.morleydev.zander.client.data.fs

import uk.co.morleydev.zander.client.data.{GetArtefactsLocation, WriteProjectSourceDetails}
import uk.co.morleydev.zander.client.model.arg.{Branch, Project}
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.store.{CacheDetails, SourceVersion}
import java.io.File
import com.lambdaworks.jacks.JacksMapper

class WriteProjectSourceDetailsToCache(getArtefactsLocation : GetArtefactsLocation,
                                        writeStringToFile : ((String, File) => Unit)) extends WriteProjectSourceDetails {
  override def apply(p: Project, c: BuildCompiler, m: BuildMode, b: Branch, v: SourceVersion): Unit = {

    val json = JacksMapper.writeValueAsString[CacheDetails](new CacheDetails(v.value))

    writeStringToFile(json, new File(getArtefactsLocation(p,c,m,b), "version.json"))
  }
}
