package uk.co.morleydev.zander.client.controller


import uk.co.morleydev.zander.client.model.arg.Compiler._
import uk.co.morleydev.zander.client.model.arg.Operation._
import uk.co.morleydev.zander.client.model.arg.BuildMode._
import uk.co.morleydev.zander.client.model.arg.Project

trait Controller extends ((Project, Compiler, BuildMode) => Unit) {
  override def apply(project : Project, compiler : Compiler, buildMode : BuildMode)
}
