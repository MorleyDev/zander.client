package uk.co.morleydev.zander.client.test.util

import java.io.File
import org.apache.commons.io.FileUtils
import uk.co.morleydev.zander.client.test.gen.GenNative

class TemporaryDirectory(create : Boolean = false) extends AutoCloseable  {

  object freeNames {
    private var generatedAlready = List[File]()

    private def generate = Iterator.continually(new File(GenNative.genAlphaNumericString(10, 20)))
      .dropWhile(f => f.exists() || generatedAlready.contains(f))
      .take(1)
      .toList
      .head

    def getFreeName: File = {
      synchronized {
        val result = generate
        generatedAlready = generatedAlready ++ List[File](result)
        return result
      }
    }
  }

  val file = freeNames.getFreeName
  if (create)
    file.mkdirs()

  def sub(s : String) : File =
    new File(file.getAbsoluteFile, s)

  override def close(): Unit = {
    if ( file.exists() )
      FileUtils.deleteDirectory(file)
  }
}
