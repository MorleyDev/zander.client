package uk.co.morleydev.zander.client.service

import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.Project

trait DownloadAcquireInstallProjectArtefacts extends ((Project, BuildCompiler, BuildMode) => Unit)

