package uk.co.morleydev.zander.client.controller

import uk.co.morleydev.zander.client.model.arg.{Operation, Compiler, BuildMode}

trait InstallController {
  def invoke(project : String,
             operation : Operation.Operation,
             compiler : Compiler.Compiler,
             buildMode : BuildMode.BuildMode)
}
