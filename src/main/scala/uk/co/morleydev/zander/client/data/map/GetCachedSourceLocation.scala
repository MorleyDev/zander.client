package uk.co.morleydev.zander.client.data.map

import java.io.File

import uk.co.morleydev.zander.client.data.GetSourceLocation
import uk.co.morleydev.zander.client.model.arg.Project

class GetCachedSourceLocation(cacheRoot: File) extends GetSourceLocation {
  def apply(p: Project) : File =
    new File(cacheRoot, "%s/src".format(p))
}
