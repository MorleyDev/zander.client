package uk.co.morleydev.zander.client.data

import uk.co.morleydev.zander.client.model.store.SourceVersion
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.Project

trait ProjectSourceDetailsWriter extends ((Project, BuildCompiler, BuildMode, SourceVersion) => Unit)
