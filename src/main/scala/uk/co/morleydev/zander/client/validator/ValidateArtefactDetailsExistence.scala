package uk.co.morleydev.zander.client.validator

import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.Project

trait ValidateArtefactDetailsExistence extends ((Project, BuildCompiler, BuildMode) => Unit)
