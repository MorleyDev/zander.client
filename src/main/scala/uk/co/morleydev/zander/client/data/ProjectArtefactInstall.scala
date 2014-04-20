package uk.co.morleydev.zander.client.data

import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode

/**
 * The project artefact install is responsible for the installation of artefacts from a store (i.e the project cache)
 * to the local working directory
 */
trait ProjectArtefactInstall extends ((Project, BuildCompiler, BuildMode) => Unit)
