package uk.co.morleydev.zander.client.service.impl

import uk.co.morleydev.zander.client.service.ProjectSourceCompile
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.data.{ProjectSourceDetailsWriter, ProjectSourceDetailsReader, ProjectSourceInstall, ProjectSourceBuild, ProjectSourcePrebuild}
import uk.co.morleydev.zander.client.model.store.SourceVersion
import java.io.FileNotFoundException

class CachedSourceCompile(detailsReader : ProjectSourceDetailsReader,
                          pre : ProjectSourcePrebuild,
                          build : ProjectSourceBuild,
                          install : ProjectSourceInstall,
                          detailsWriter : ProjectSourceDetailsWriter) extends ProjectSourceCompile {
  override def apply(p: Project, c: BuildCompiler, m: BuildMode, v: SourceVersion) : Unit = {
    try {
      detailsReader(p, c, m)
    } catch {
      case e : FileNotFoundException =>
        pre(p, c, m)
        build(p, c, m)
        install(p, c, m)
        detailsWriter(p,c,m,v)
    }
  }
}
