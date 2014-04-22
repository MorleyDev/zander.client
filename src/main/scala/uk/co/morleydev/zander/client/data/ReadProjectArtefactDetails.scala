package uk.co.morleydev.zander.client.data

import uk.co.morleydev.zander.client.model.store.ArtefactDetails
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler

trait ReadProjectArtefactDetails extends ((Project, BuildCompiler, BuildMode) => ArtefactDetails)
