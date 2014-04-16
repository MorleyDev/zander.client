package uk.co.morleydev.zander.client.util

import scala.collection.JavaConversions
import java.io.File
import uk.co.morleydev.zander.client.data.program

class NativeProcessBuilderImpl(args : Seq[String]) extends program.NativeProcessBuilder {

  private val processBuilder = new java.lang.ProcessBuilder(JavaConversions.seqAsJavaList(args))

  override def directory(directory: String): program.NativeProcessBuilder = {

    val dirFile = new File(directory)
    if ( !dirFile.exists() )
      dirFile.mkdirs()

    processBuilder.directory(dirFile)
    this
  }

  override def start(): Process = {
    processBuilder.start()
  }
}
