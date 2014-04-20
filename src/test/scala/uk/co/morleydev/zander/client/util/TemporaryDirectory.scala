package uk.co.morleydev.zander.client.util

import java.io.File
import org.apache.commons.io.FileUtils

class TemporaryDirectory(var file : File, createDir : Boolean = false) extends AutoCloseable  {
  if (createDir)
    file.mkdirs()

  def sub(s : String) : File =
    new File(file, s)

  override def close(): Unit = {
    if ( file.exists() )
      FileUtils.deleteDirectory(file)
  }
}
