package uk.co.morleydev.zander.client.util

import java.io.File
import org.apache.commons.io.FileUtils
import uk.co.morleydev.zander.client.gen.GenNative

class TemporaryDirectory(createDir : Boolean = false) extends AutoCloseable  {
  val file =  Iterator.continually(new File(GenNative.genAlphaNumericString(1, 10))).dropWhile(_.exists()).take(1).toList.head
  if (createDir)
    file.mkdirs()

  def sub(s : String) : File =
    new File(file.getAbsoluteFile, s)

  override def close(): Unit = {
    if ( file.exists() )
      FileUtils.deleteDirectory(file)
  }
}
