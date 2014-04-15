package uk.co.morleydev.zander.client.model

import uk.co.morleydev.zander.client.model.arg.Operation
import Operation.Operation
import arg.Compiler.Compiler
import uk.co.morleydev.zander.client.model.arg.BuildMode
import BuildMode.BuildMode

class Arguments(val operation : Operation,
                val project : String,
                val compiler : Compiler,
                val mode : BuildMode) {

}
