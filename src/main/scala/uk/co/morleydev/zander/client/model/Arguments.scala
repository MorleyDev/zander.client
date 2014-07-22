package uk.co.morleydev.zander.client.model

import uk.co.morleydev.zander.client.model.arg.{Branch, Project, Operation, BuildMode}
import Operation.Operation
import arg.BuildCompiler.BuildCompiler
import BuildMode.BuildMode

case class OperationArguments(project : Project,
                            compiler : BuildCompiler,
                            mode : BuildMode,
                            branch : Branch = new Branch("master"))

case class Arguments(operation : Operation,
                     operationArgs : OperationArguments)
