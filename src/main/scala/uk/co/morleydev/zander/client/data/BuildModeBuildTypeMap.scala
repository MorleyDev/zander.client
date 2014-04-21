package uk.co.morleydev.zander.client.data

import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode

trait BuildModeBuildTypeMap extends (BuildMode => String)
