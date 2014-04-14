package uk.co.morleydev.zander.client

import uk.co.morleydev.zander.client.model.Operation._
import uk.co.morleydev.zander.client.model.Compiler._
import uk.co.morleydev.zander.client.model.BuildMode._

class Arguments(val operation : Operation,
                val project : String,
                val compiler : Compiler,
                val mode : BuildMode) {

}
