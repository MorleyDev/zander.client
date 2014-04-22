package uk.co.morleydev.zander.client.service

import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler

trait PurgeProjectArtefacts extends ((Project, BuildCompiler, BuildMode) => Unit)
