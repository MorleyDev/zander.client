package uk.co.morleydev.zander.client

import uk.co.morleydev.zander.client.model.{Arguments, Configuration, ExitCodes}
import scala.io.Source
import com.lambdaworks.jacks.JacksMapper
import uk.co.morleydev.zander.client.model.arg.{Project, Operation, Compiler, BuildMode}
import uk.co.morleydev.zander.client.controller.ControllerFactoryImpl
import java.io.FileNotFoundException
import uk.co.morleydev.zander.client.util.NativeProcessBuilderImpl
import uk.co.morleydev.zander.client.data.program.{NativeProcessBuilder, NativeProcessBuilderFactory}

object Main {

  object NativeProcessBuilderFactoryImpl$ extends NativeProcessBuilderFactory {
    override def apply(commands :  Seq[String]): NativeProcessBuilder =
      new NativeProcessBuilderImpl(commands)
  }

  def main(args : Array[String],
           configFile : String,
           exit : Int => Unit,
           processBuilderFactory : NativeProcessBuilderFactory) {

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
    val project = try { new Project(args(1)) } catch { case e : IllegalArgumentException => exit(ExitCodes.InvalidProject); return }
    val compiler = extractEnum(Compiler, args(2), ExitCodes.InvalidCompiler)
    val buildMode =extractEnum(BuildMode, args(3), ExitCodes.InvalidBuildMode)
    if (operation == null || compiler == null || buildMode == null)
      return

    val arguments = new Arguments(operation, project, compiler, buildMode)

    val configJson = try {
      Source.fromFile(configFile).getLines().mkString
    } catch {
      case e : FileNotFoundException =>
        println("Warning: Could not open config file " + configFile + ", using defaults")
        "{ }"
    }
    val config = JacksMapper.readValue[Configuration](configJson)

    val program = new Program(new ControllerFactoryImpl(processBuilderFactory))
    val returnCode = program.run(arguments, config)
    exit(returnCode)
  }

  def main(args : Array[String]) {
    main(args, "config.json", System.exit, NativeProcessBuilderFactoryImpl$)
  }
}
