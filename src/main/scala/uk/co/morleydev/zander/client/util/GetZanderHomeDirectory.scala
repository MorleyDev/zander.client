package uk.co.morleydev.zander.client.util

import java.io.File

object GetZanderHomeDirectory extends (() => String) {

  private def getDefaultHomeDir =
    new File(System.getProperty("user.home"), ".zander").getAbsolutePath

  override def apply() : String =
    sys.env.getOrElse("ZANDER_HOME", getDefaultHomeDir)
}
