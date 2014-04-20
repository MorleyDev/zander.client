package uk.co.morleydev.zander.client.data

import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.store.SourceDetails

trait ProjectSourceDetailsReader extends ((Project, BuildCompiler, BuildMode) => SourceDetails)
