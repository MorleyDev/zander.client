package uk.co.morleydev.zander.client.data.map

import uk.co.morleydev.zander.client.data.BuildModeBuildTypeMap
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.BuildMode
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode

object CMakeBuildModeBuildTypeMap extends BuildModeBuildTypeMap {

  private val modeCMakeMap = Map[BuildMode, String](
    BuildMode.Debug -> "Debug",
    BuildMode.Release -> "Release"
  )

  override def apply(mode: BuildMode): String = modeCMakeMap(mode)
}
