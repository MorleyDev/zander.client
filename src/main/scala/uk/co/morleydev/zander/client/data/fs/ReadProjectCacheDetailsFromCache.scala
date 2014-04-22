package uk.co.morleydev.zander.client.data.fs

import uk.co.morleydev.zander.client.data.ReadProjectCacheDetails
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.store.CacheDetails
import java.io.File
import com.lambdaworks.jacks.JacksMapper

class ReadProjectCacheDetailsFromCache(cache : File, readFileToString : (File => String))
  extends ReadProjectCacheDetails {

  override def apply(project : Project, compiler : BuildCompiler, mode : BuildMode) : CacheDetails = {
    val json = readFileToString(new File(cache, "%s/%s.%s/version.json".format(project, compiler, mode)))

    JacksMapper.readValue[CacheDetails](json)
  }
}
