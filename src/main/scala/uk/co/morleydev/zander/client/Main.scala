package uk.co.morleydev.zander.client

import uk.co.morleydev.zander.client.model.{Arguments, Configuration, ExitCodes}
import scala.io.Source
import com.lambdaworks.jacks.JacksMapper
import uk.co.morleydev.zander.client.model.arg.{Project, Operation, Compiler, BuildMode}
import uk.co.morleydev.zander.client.controller.ControllerFactoryImpl
import java.io.{File, FileNotFoundException}
import uk.co.morleydev.zander.client.util.{Log, NativeProcessBuilderImpl}
import uk.co.morleydev.zander.client.data.{NativeProcessBuilderFactory, NativeProcessBuilder}
import java.util.UUID

object Main {

  object NativeProcessBuilderFactoryImpl extends NativeProcessBuilderFactory {
    override def apply(commands :  Seq[String]): NativeProcessBuilder =
      new NativeProcessBuilderImpl(commands)
  }

  def main(args : Array[String],
           configFile : String,
           exit : Int => Unit,
           processBuilderFactory : NativeProcessBuilderFactory,
           temporaryDirectory : File) {

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
    val buildMode = extractEnum(BuildMode, args(3), ExitCodes.InvalidBuildMode)
    if (operation == null || compiler == null || buildMode == null)
      return

    val arguments = new Arguments(operation, project, compiler, buildMode)

    val configJson = try {
      val file = Source.fromFile(configFile)
      val json = file.getLines().mkString
      file.close()
      json
    } catch {
      case e : FileNotFoundException =>
        println("Warning: Could not open config file " + configFile + ", using defaults")
        JacksMapper.writeValueAsString(new Configuration())
    }
    val config = JacksMapper.readValue[Configuration](configJson)

    val program = new Program(new ControllerFactoryImpl(processBuilderFactory, temporaryDirectory))
    val returnCode = program.run(arguments, config)
    exit(returnCode)
  }

  def main(args : Array[String]) {

    def GetProgramDirectory() : String =
        new File(getClass.getProtectionDomain.getCodeSource.getLocation.getPath).getParentFile.getAbsolutePath

    val temporaryDirectory = new File("zander-" + UUID.randomUUID().toString + "-tmp")

    try {
      main(args, new File(GetProgramDirectory(), "config.json").getAbsolutePath,
        code => {
          Log("Exiting with code", code); System.exit(code)
        },
        NativeProcessBuilderFactoryImpl,
        temporaryDirectory)
    } finally {
      if (temporaryDirectory.exists())
        temporaryDirectory.delete()
    }
  }
}
