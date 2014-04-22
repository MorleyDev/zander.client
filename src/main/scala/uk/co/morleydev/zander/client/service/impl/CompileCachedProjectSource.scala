package uk.co.morleydev.zander.client.service.impl

import uk.co.morleydev.zander.client.service.CompileProjectSource
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.data.{WriteProjectSourceDetails, ReadProjectCacheDetails, InstallProjectCache, BuildProjectSource, PreBuildProjectSource}
import uk.co.morleydev.zander.client.model.store.SourceVersion
import java.io.FileNotFoundException

class CompileCachedProjectSource(detailsReader : ReadProjectCacheDetails,
                          deleteDirectory : ((Project, BuildCompiler, BuildMode) => Unit),
                          pre : PreBuildProjectSource,
                          build : BuildProjectSource,
                          install : InstallProjectCache,
                          detailsWriter : WriteProjectSourceDetails) extends CompileProjectSource {
  override def apply(p: Project, c: BuildCompiler, m: BuildMode, v: SourceVersion) : Unit = {
    val cachedVersion = try {
      new SourceVersion(detailsReader(p, c, m).version)
    } catch {
      case e : FileNotFoundException => null
    }

    if (cachedVersion != v) {
      if (cachedVersion != null)
        deleteDirectory(p,c,m)

      pre(p, c, m)
      build(p, c, m)
      install(p, c, m)
      detailsWriter(p,c,m,v)
    }
  }
}
