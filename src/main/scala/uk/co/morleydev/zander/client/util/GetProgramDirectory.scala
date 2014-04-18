package uk.co.morleydev.zander.client.util

import java.io.File

object GetProgramDirectory extends (() => File) {
  override def apply() : File =
    new File(getClass.getProtectionDomain.getCodeSource.getLocation.getPath).getParentFile
}
