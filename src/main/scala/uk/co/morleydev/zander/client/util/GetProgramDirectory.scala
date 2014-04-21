package uk.co.morleydev.zander.client.util

import java.io.File

object GetProgramDirectory extends (() => String) {
  override def apply() : String = {
    new File(System.getProperty("user.home"), ".zander").getAbsolutePath
  }
}
