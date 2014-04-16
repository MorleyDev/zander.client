package uk.co.morleydev.zander.client.controller


import uk.co.morleydev.zander.client.model.arg.Compiler._
import uk.co.morleydev.zander.client.model.arg.Operation._
import uk.co.morleydev.zander.client.model.arg.BuildMode._

trait Controller extends ((Operation, String, Compiler, BuildMode) => Unit) {
  override def apply(operation : Operation, project : String, compiler : Compiler, buildMode : BuildMode)
}
