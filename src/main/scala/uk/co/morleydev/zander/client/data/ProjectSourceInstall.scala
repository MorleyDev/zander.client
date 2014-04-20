package uk.co.morleydev.zander.client.data


import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode

/**
 * The project source install is responsible for the installation of project files created by the build
 */
trait ProjectSourceInstall extends ((Project, BuildCompiler, BuildMode) => Unit)

