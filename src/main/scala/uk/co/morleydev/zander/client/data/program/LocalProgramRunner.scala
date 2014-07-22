package uk.co.morleydev.zander.client.data.program

import java.io.File
import uk.co.morleydev.zander.client.data.NativeProcessBuilderFactory
import scala.io.Source
import uk.co.morleydev.zander.client.util.Log

class LocalProgramRunner(processBuilderFactory : NativeProcessBuilderFactory) extends ProgramRunner {
  override def apply(command : Seq[String], directory : File) : Int = {

    val process = processBuilderFactory(command)
      .directory(directory)
      .start()

    Source.fromInputStream(process.getInputStream).getLines().foreach(Log.message(_))
    Source.fromInputStream(process.getErrorStream).getLines().foreach(Log.warning(_))

    process.waitFor()
  }
}
