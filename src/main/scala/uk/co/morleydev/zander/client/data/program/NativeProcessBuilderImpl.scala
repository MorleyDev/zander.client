package uk.co.morleydev.zander.client.data.program

import java.io.File

import uk.co.morleydev.zander.client.data.NativeProcessBuilder
import uk.co.morleydev.zander.client.util.Log

import scala.collection.JavaConversions

class NativeProcessBuilderImpl(args : Seq[String]) extends NativeProcessBuilder {

  private val processBuilder = new java.lang.ProcessBuilder(JavaConversions.seqAsJavaList(args))

  override def directory(directory: File): NativeProcessBuilder = {

    Log.message("Running %s in directory %s".format(args.mkString(" "), directory.getPath))

    if ( !directory.exists() )
      directory.mkdirs()

    processBuilder.directory(directory)
    this
  }

  override def start(): Process = {
    processBuilder.start()
  }
}
