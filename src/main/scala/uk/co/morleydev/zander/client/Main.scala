package uk.co.morleydev.zander.client

import uk.co.morleydev.zander.client.model._
import java.net.URL

object Main {

  private val program = new Program()

  def main(args : Array[String], configFile : String, exit : Int => Unit) {

    val arguments = new Arguments(Operation.withName(args(0)),
                                  args(1),
                                  Compiler.withName(args(2)),
                                  BuildMode.withName(args(3)))

    val config = new Configuration(new URL("http://zander.morleydev.co.uk"))

    val returnCode = program.run(arguments, config)
    exit(returnCode)
  }

  def main(args : Array[String]) {
    main(args, "config.json", System.exit)
  }
}
