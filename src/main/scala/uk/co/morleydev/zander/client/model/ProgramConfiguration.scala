package uk.co.morleydev.zander.client.model

import com.fasterxml.jackson.annotation.JsonProperty

case class ProgramConfiguration(@JsonProperty("git") git : String = "git",
                                @JsonProperty("cmake") cmake : String = "cmake")
