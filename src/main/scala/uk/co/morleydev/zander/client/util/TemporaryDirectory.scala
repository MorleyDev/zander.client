package uk.co.morleydev.zander.client.util

import java.io.File
import java.util.UUID

import org.apache.commons.io.FileUtils

class TemporaryDirectory extends File(Iterator.continually(new File(System.getProperty("java.io.tmpdir"), "znd-%s".format(UUID.randomUUID().toString)))
  .dropWhile(_.exists())
  .take(1)
  .toSeq
  .head.getAbsolutePath) with AutoCloseable {

  mkdirs()

  override def close() =
    if (!FileUtils.deleteQuietly(this))
      Log.error("Could not delete temporary directory " + getName)
}

