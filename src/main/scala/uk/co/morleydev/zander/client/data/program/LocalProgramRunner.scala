package uk.co.morleydev.zander.client.data.program

import java.io.File
import uk.co.morleydev.zander.client.data.NativeProcessBuilderFactory
import scala.io.Source

class LocalProgramRunner(processBuilderFactory : NativeProcessBuilderFactory) extends ProgramRunner {
  override def apply(command : Seq[String], directory : File) : Int = {

    val process = processBuilderFactory(command)
      .directory(directory)
      .start()

    Source.fromInputStream(process.getInputStream).getLines().foreach(println(_))
    Source.fromInputStream(process.getErrorStream).getLines().foreach(println(_))

    process.waitFor()
  }
}
