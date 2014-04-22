package uk.co.morleydev.zander.client.data.map

import uk.co.morleydev.zander.client.data.SplitFileNameToProjectDetails
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.{BuildMode, BuildCompiler, Project}

object SplitJsonFileNameToProjectDetails extends SplitFileNameToProjectDetails {
  override def apply(name: String): (Project, BuildCompiler, BuildMode) = {
    val parts = name.split('.')
    val withoutExtension = parts.dropRight(1)
    val withoutMode = withoutExtension.dropRight(1)
    val withoutCompiler = withoutMode.dropRight(1)

    val mode = withoutExtension.takeRight(1).apply(0)
    val compiler = withoutMode.takeRight(1).apply(0)
    val project = withoutCompiler.mkString(".")

    (new Project(project), BuildCompiler.withName(compiler), BuildMode.withName(mode))
  }
}
