package uk.co.morleydev.zander.client.service.impl

import uk.co.morleydev.zander.client.service.ProjectSourceCompile
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.Compiler.Compiler
import uk.co.morleydev.zander.client.data.{ProjectSourceInstall, ProjectSourceBuild, ProjectSourcePrebuild}

class CachedSourceCompile(pre : ProjectSourcePrebuild,
                          build : ProjectSourceBuild,
                          install : ProjectSourceInstall) extends ProjectSourceCompile {
  override def apply(p: Project, c: Compiler, m: BuildMode): Unit = {
    pre(p,c,m)
    build(p,c,m)
    install(p,c,m)
  }
}
