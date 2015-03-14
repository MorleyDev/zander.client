package uk.co.morleydev.zander.client.data.map

import java.io.File

import uk.co.morleydev.zander.client.data.GetArtefactsLocation
import uk.co.morleydev.zander.client.model.arg.BuildCompiler._
import uk.co.morleydev.zander.client.model.arg.BuildMode._
import uk.co.morleydev.zander.client.model.arg.{Branch, Project}

class GetCachedArtefactsLocation(cacheRoot: File) extends GetArtefactsLocation {
  def apply(p: Project, c: BuildCompiler, m: BuildMode, b: Branch) : File =
    new File(cacheRoot, "%s/bin/%s/%s.%s".format(p,b,c,m))
}


