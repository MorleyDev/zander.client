package uk.co.morleydev.zander.client.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.File
import uk.co.morleydev.zander.client.util.GetProgramDirectory

case class Configuration(@JsonProperty("server") server : String = "http://zander.morleydev.co.uk",
                         @JsonProperty("programs") programs : ProgramConfiguration = new ProgramConfiguration(),
                         @JsonProperty("cache") cache : String = new File(GetProgramDirectory(), "cache").getAbsolutePath)
