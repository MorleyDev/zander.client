package uk.co.morleydev.zander.client.data.fs

import uk.co.morleydev.zander.client.data.{GetArtefactsLocation, ReadProjectCacheDetails}
import uk.co.morleydev.zander.client.model.arg.{Branch, Project}
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.store.CacheDetails
import java.io.File
import com.lambdaworks.jacks.JacksMapper

class ReadProjectCacheDetailsFromCache(getArtefactsLocation : GetArtefactsLocation, readFileToString : (File => String))
  extends ReadProjectCacheDetails {

  override def apply(project : Project, compiler : BuildCompiler, mode : BuildMode, branch : Branch) : CacheDetails = {
    val json = readFileToString(new File(getArtefactsLocation(project, compiler, mode, branch), "version.json"))

    JacksMapper.readValue[CacheDetails](json)
  }
}
