package uk.co.morleydev.zander.client

import uk.co.morleydev.zander.client.model.arg.BuildCompiler._
import uk.co.morleydev.zander.client.model.arg.BuildMode._
import uk.co.morleydev.zander.client.model.arg.Project

package object controller {
  type Controller = ((Project, BuildCompiler, BuildMode) => Unit)
}
