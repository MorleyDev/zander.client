package uk.co.morleydev.zander.client

import uk.co.morleydev.zander.client.model.{Arguments, Configuration, ExitCodes}
import scala.io.Source
import com.lambdaworks.jacks.JacksMapper
import uk.co.morleydev.zander.client.model.arg.{Project, Operation, BuildCompiler, BuildMode}
import java.io.{PrintWriter, File, FileNotFoundException}
import uk.co.morleydev.zander.client.util.{Log, NativeProcessBuilderImpl, GetProgramDirectory}
import uk.co.morleydev.zander.client.util.Using.using
import uk.co.morleydev.zander.client.data.{NativeProcessBuilderFactory, NativeProcessBuilder}
import java.util.UUID
import org.apache.commons.io.FileUtils
import uk.co.morleydev.zander.client.controller.impl.ControllerFactoryImpl

object Main {

  class InvalidArgumentsException(val message : String) extends Exception
  class MissingArgumentsException extends InvalidArgumentsException("Missing arguments, expected [operation] [project] [compiler] [build mode]")
  class InvalidOperationException(val operation : String) extends InvalidArgumentsException("Operation " + operation + " is not valid operation")
  class InvalidProjectException(val project : String) extends InvalidArgumentsException("Project " + project + " is not valid project")
  class InvalidCompilerException(val compiler : String) extends InvalidArgumentsException("Compiler " + compiler + " is not valid compiler")
  class InvalidBuildModeException(val mode : String) extends InvalidArgumentsException("BuildMode " + mode + " is not valid build mode")

  object NativeProcessBuilderFactoryImpl extends NativeProcessBuilderFactory {
    override def apply(commands :  Seq[String]): NativeProcessBuilder =
      new NativeProcessBuilderImpl(commands)
  }

  object ArgumentParser {
    private def extractEnum(enum: Enumeration, value: String): enum.Value = {
      try {
        enum.withName(value)
      } catch {
        case e: NoSuchElementException =>
          return null
      }
    }

    def apply(args : Array[String]): Arguments = {
      if (args.size != 4)
        throw new MissingArgumentsException()

      val operation = extractEnum(Operation, args(0))
      val project = try {
        new Project(args(1))
      } catch {
        case e: IllegalArgumentException => null
      }
      val compiler = extractEnum(BuildCompiler, args(2))
      val buildMode = extractEnum(BuildMode, args(3))

      if (operation == null)
        throw new InvalidOperationException(args(0))
      if (project == null)
        throw new InvalidProjectException(args(1))
      if (compiler == null)
        throw new InvalidCompilerException(args(2))
      if (buildMode == null)
        throw new InvalidBuildModeException(args(3))

      new Arguments(operation, project, compiler, buildMode)
    }
  }

  def main(args : Array[String],
           configFile : String,
           processBuilderFactory : NativeProcessBuilderFactory,
           temporaryDirectory : File,
           workingDirectory: File) : Int = {

    val arguments = try ArgumentParser(args) catch {
      case e: InvalidArgumentsException =>
        Log.error(e.message)
        e match {
          case e: MissingArgumentsException => return ExitCodes.InvalidArgumentCount
          case e: InvalidOperationException => return ExitCodes.InvalidOperation
          case e: InvalidProjectException => return ExitCodes.InvalidProject
          case e: InvalidCompilerException => return ExitCodes.InvalidCompiler
          case e: InvalidBuildModeException => return ExitCodes.InvalidBuildMode
        }
    }

    val configJson = try {
      val file = Source.fromFile(configFile)
      val json = file.getLines().mkString("\n")
      file.close()
      json
    } catch {
      case e : FileNotFoundException =>
        Log.warning("Could not open config file %s, using defaults".format(configFile))
        val configJson = JacksMapper.writeValueAsString(new Configuration())
        val configParentPath = new File(configFile).getParentFile
        if (!configParentPath.exists())
          configParentPath.mkdirs()

        try { using(new PrintWriter(configFile)) { write => write.write(configJson) } } catch { case _: Throwable => }
        configJson
    }
    val config = JacksMapper.readValue[Configuration](configJson)

    val program = new Program(new ControllerFactoryImpl(processBuilderFactory, temporaryDirectory, workingDirectory))
    program.run(arguments, config)
  }

  def main(args : Array[String]) {

    Log.enableLogging()

    class TemporaryDirectory extends AutoCloseable {
      val dirFile = Iterator.continually(new File("zander-" + args.mkString("-") + UUID.randomUUID().toString + "-tmp"))
        .dropWhile(_.exists())
        .take(1)
        .toSeq
        .head

      dirFile.mkdirs()

      override def close() = {
        FileUtils.deleteDirectory(dirFile)
      }
    }

    val responseCode = using(new TemporaryDirectory) {
      temporaryDirectory =>

        main(args,
          new File(GetProgramDirectory(), "config.json").getAbsolutePath,
          NativeProcessBuilderFactoryImpl,
          temporaryDirectory.dirFile,
          new File("").getAbsoluteFile)
    }

    System.exit(responseCode)
  }
}
