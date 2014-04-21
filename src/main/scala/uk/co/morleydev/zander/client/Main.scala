package uk.co.morleydev.zander.client

import uk.co.morleydev.zander.client.model.{Arguments, Configuration, ExitCodes}
import scala.io.Source
import com.lambdaworks.jacks.JacksMapper
import uk.co.morleydev.zander.client.model.arg.{Project, Operation, BuildCompiler, BuildMode}
import uk.co.morleydev.zander.client.controller.ControllerFactoryImpl
import java.io.{PrintWriter, File, FileNotFoundException}
import uk.co.morleydev.zander.client.util.{Log, NativeProcessBuilderImpl, GetProgramDirectory}
import uk.co.morleydev.zander.client.util.Using.using
import uk.co.morleydev.zander.client.data.{NativeProcessBuilderFactory, NativeProcessBuilder}
import java.util.UUID
import org.apache.commons.io.FileUtils

object Main {

  object NativeProcessBuilderFactoryImpl extends NativeProcessBuilderFactory {
    override def apply(commands :  Seq[String]): NativeProcessBuilder =
      new NativeProcessBuilderImpl(commands)
  }

  def main(args : Array[String],
           configFile : String,
           processBuilderFactory : NativeProcessBuilderFactory,
           temporaryDirectory : File,
           workingDirectory: File) : Int = {

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
    val compiler = extractEnum(BuildCompiler, args(2))
    val buildMode = extractEnum(BuildMode, args(3))

    if (operation == null) return ExitCodes.InvalidOperation
    if (project == null) return ExitCodes.InvalidProject
    if(compiler == null) return ExitCodes.InvalidCompiler
    if(buildMode == null) return ExitCodes.InvalidBuildMode

    val arguments = new Arguments(operation, project, compiler, buildMode)

    val configJson = try {
      val file = Source.fromFile(configFile)
      val json = file.getLines().mkString("\n")
      file.close()
      json
    } catch {
      case e : FileNotFoundException =>
        println("Warning: Could not open config file " + configFile + ", using defaults")
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

    class TemporaryDirectory extends AutoCloseable {
      val dirFile = Iterator.continually(new File("zander-" + UUID.randomUUID().toString + "-tmp"))
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

    Log("Exiting with code %d".format(responseCode))
    System.exit(responseCode)
  }
}
