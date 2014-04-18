package uk.co.morleydev.zander.client.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.File

private object GetProgramDirectory extends (() => String) {
  override def apply(): String = 
    new File(getClass.getProtectionDomain.getCodeSource.getLocation.getPath).getParentFile.getAbsolutePath
}

case class Configuration(@JsonProperty("server") server : String = "http://zander.morleydev.co.uk",
                         @JsonProperty("programs") programs : ProgramConfiguration = new ProgramConfiguration(),
                         @JsonProperty("cache") cache : String = new File(GetProgramDirectory(), "cache").getAbsolutePath)
