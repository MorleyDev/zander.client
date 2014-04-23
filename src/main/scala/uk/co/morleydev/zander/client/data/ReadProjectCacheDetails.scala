package uk.co.morleydev.zander.client.data

import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.store.CacheDetails

trait ReadProjectCacheDetails extends ((Project, BuildCompiler, BuildMode) => CacheDetails)