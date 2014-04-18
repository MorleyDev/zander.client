package uk.co.morleydev.zander.client

import uk.co.morleydev.zander.client.model.{Arguments, Configuration, ExitCodes}
import scala.io.Source
import com.lambdaworks.jacks.JacksMapper
import uk.co.morleydev.zander.client.model.arg.{Project, Operation, Compiler, BuildMode}
import uk.co.morleydev.zander.client.controller.ControllerFactoryImpl
import java.io.{File, FileNotFoundException}
import uk.co.morleydev.zander.client.util.{Log, NativeProcessBuilderImpl, GetProgramDirectory}
import uk.co.morleydev.zander.client.util.Using.using
import uk.co.morleydev.zander.client.data.{NativeProcessBuilderFactory, NativeProcessBuilder}
import java.util.UUID

object Main {

  object NativeProcessBuilderFactoryImpl extends NativeProcessBuilderFactory {
    override def apply(commands :  Seq[String]): NativeProcessBuilder =
      new NativeProcessBuilderImpl(commands)
  }

  def main(args : Array[String],
           configFile : String,
           processBuilderFactory : NativeProcessBuilderFactory,
           temporaryDirectory : File) : Int = {

    def extractEnum(enum : Enumeration, value: String): enum.Value = {
      try {
        enum.withName(value)
      } catch {
        case e: NoSuchElementException =>
          return null
      }
    }

    val operation = extractEnum(Operation, args(0))
    val project = try { new Project(args(1)) } catch { case e : IllegalArgumentException => null }
    val compiler = extractEnum(Compiler, args(2))
    val buildMode = extractEnum(BuildMode, args(3))

    if (operation == null) return ExitCodes.InvalidOperation
    if (project == null) return ExitCodes.InvalidProject
    if(compiler == null) return ExitCodes.InvalidCompiler
    if(buildMode == null) return ExitCodes.InvalidBuildMode

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
    program.run(arguments, config)
  }

  def main(args : Array[String]) {

    class TemporaryDirectory extends AutoCloseable {
      val dirFile = Iterator.continually(new File("zander-" + UUID.randomUUID().toString + "-tmp"))
        .dropWhile(_.exists())
        .take(1)
        .toSeq
        .head

      dirFile.mkdirs()

      override def close() = {
        dirFile.delete()
      }
    }

    val responseCode = using(new TemporaryDirectory) {
      temporaryDirectory =>

        main(args,
          new File(GetProgramDirectory(), "config.json").getAbsolutePath,
          NativeProcessBuilderFactoryImpl,
          temporaryDirectory.dirFile)
    }

    Log("Exiting with code %d".format(responseCode))
    System.exit(responseCode)
  }
}
