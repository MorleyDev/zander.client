package uk.co.morleydev.zander.client.service.impl

import uk.co.morleydev.zander.client.service.CompileProjectSource
import uk.co.morleydev.zander.client.model.arg.{Branch, Project}
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
  override def apply(p: Project, c: BuildCompiler, m: BuildMode, b: Branch,v: SourceVersion) : Unit = {
    val cachedVersion = try {
      new SourceVersion(detailsReader(p, c, m, b).version)
    } catch {
      case e : FileNotFoundException => null
    }

    if (cachedVersion != v) {
      if (cachedVersion != null)
        deleteDirectory(p,c,m)

      pre(p, c, m, b)
      build(p, c, m)
      install(p, c, m)
      detailsWriter(p,c,m,b,v)
    }
  }
}
