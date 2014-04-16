package uk.co.morleydev.zander.client

import uk.co.morleydev.zander.client.model.{Arguments, Configuration, ExitCodes}
import scala.io.Source
import com.lambdaworks.jacks.JacksMapper
import uk.co.morleydev.zander.client.model.arg.{Operation, Compiler, BuildMode}
import uk.co.morleydev.zander.client.validator.ProjectValidator
import uk.co.morleydev.zander.client.controller.ControllerFactoryImpl

object Main {

  private val program = new Program(ProjectValidator, ControllerFactoryImpl)

  def main(args : Array[String], configFile : String, exit : Int => Unit) {

    def extractEnum(enum : Enumeration, value: String, failureCode: Int): enum.Value = {
      try {
        enum.withName(value)
      } catch {
        case e: NoSuchElementException =>
          exit(failureCode)
          return null
      }
    }

    val operation = extractEnum(Operation, args(0), ExitCodes.InvalidOperation)
    val project = args(1)
    val compiler = extractEnum(Compiler, args(2), ExitCodes.InvalidCompiler)
    val buildMode =extractEnum(BuildMode, args(3), ExitCodes.InvalidBuildMode)
    if (operation == null || compiler == null || buildMode == null)
      return

    val arguments = new Arguments(operation, project, compiler, buildMode)

    val configJson = Source.fromFile(configFile).getLines().mkString
    val config = JacksMapper.readValue[Configuration](configJson)

    val returnCode = program.run(arguments, config)
    exit(returnCode)
  }

  def main(args : Array[String]) {
    main(args, "config.json", System.exit)
  }
}
