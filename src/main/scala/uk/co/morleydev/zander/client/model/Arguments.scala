package uk.co.morleydev.zander.client.model

import uk.co.morleydev.zander.client.model.arg.{Project, Operation, BuildMode}
import Operation.Operation
import arg.Compiler.Compiler
import BuildMode.BuildMode

class Arguments(val operation : Operation,
                val project : Project,
                val compiler : Compiler,
                val mode : BuildMode) {

}
