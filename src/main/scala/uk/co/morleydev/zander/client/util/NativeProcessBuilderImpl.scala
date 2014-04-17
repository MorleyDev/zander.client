package uk.co.morleydev.zander.client.util

import scala.collection.JavaConversions
import java.io.File
import uk.co.morleydev.zander.client.data.NativeProcessBuilder

class NativeProcessBuilderImpl(args : Seq[String]) extends NativeProcessBuilder {

  private val processBuilder = new java.lang.ProcessBuilder(JavaConversions.seqAsJavaList(args))

  override def directory(directory: File): NativeProcessBuilder = {

    Log("Running", args.mkString(" "), "in directory", directory.getPath)

    if ( !directory.exists() )
      directory.mkdirs()

    processBuilder.directory(directory)
    this
  }

  override def start(): Process = {
    processBuilder.start()
  }
}
