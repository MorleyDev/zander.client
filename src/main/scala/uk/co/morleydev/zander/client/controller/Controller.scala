package uk.co.morleydev.zander.client.controller


import uk.co.morleydev.zander.client.model.arg.Compiler._
import uk.co.morleydev.zander.client.model.arg.Operation._
import uk.co.morleydev.zander.client.model.arg.BuildMode._
import uk.co.morleydev.zander.client.model.arg.Project

trait Controller extends ((Operation, Project, Compiler, BuildMode) => Unit) {
  override def apply(operation : Operation, project : Project, compiler : Compiler, buildMode : BuildMode)
}
