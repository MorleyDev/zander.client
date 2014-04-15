package uk.co.morleydev.zander.client

import uk.co.morleydev.zander.client.model._
import java.net.URL
import scala.io.Source
import com.lambdaworks.jacks.JacksMapper
import uk.co.morleydev.zander.client.model.arg.{Operation, Compiler, BuildMode}
import uk.co.morleydev.zander.client.validator.ProjectValidator

object Main {

  private val program = new Program(ProjectValidator)

  def main(args : Array[String], configFile : String, exit : Int => Unit) {

    val arguments = new Arguments(Operation.withName(args(0)),
                                  args(1),
                                  Compiler.withName(args(2)),
                                  BuildMode.withName(args(3)))


    val configJson = Source.fromFile(configFile).getLines.mkString
    val config = JacksMapper.readValue[Configuration](configJson)

    val returnCode = program.run(arguments, config)
    exit(returnCode)
  }

  def main(args : Array[String]) {
    main(args, "config.json", System.exit)
  }
}
