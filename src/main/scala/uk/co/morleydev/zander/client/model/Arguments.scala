package uk.co.morleydev.zander.client.model

import uk.co.morleydev.zander.client.model.arg.{Project, Operation, BuildMode}
import Operation.Operation
import arg.BuildCompiler.BuildCompiler
import BuildMode.BuildMode

class Arguments(val operation : Operation,
                val project : Project,
                val compiler : BuildCompiler,
                val mode : BuildMode) {

}
