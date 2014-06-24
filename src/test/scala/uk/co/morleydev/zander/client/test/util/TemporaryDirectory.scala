package uk.co.morleydev.zander.client.test.util

import java.io.File
import org.apache.commons.io.FileUtils
import uk.co.morleydev.zander.client.test.gen.GenNative

class TemporaryDirectory(create : Boolean = false) extends AutoCloseable  {

  object freeNames {
    private var generatedAlready = List[File]()

    def getFreeName(create: Boolean): File = synchronized {
      val result = Iterator.continually(new File(GenNative.genAlphaNumericString(10, 20)))
        .dropWhile(f => f.exists() || generatedAlready.contains(f))
        .take(1)
        .toList
        .head
      generatedAlready ++= List[File](result)

      if (create)
        result.mkdirs()

      return result
    }
  }

  val file = freeNames.getFreeName(create)

  def sub(s : String) : File =
    new File(file.getAbsoluteFile, s)

  override def close(): Unit = {
    if ( file.exists() )
      FileUtils.deleteDirectory(file)
  }
}
