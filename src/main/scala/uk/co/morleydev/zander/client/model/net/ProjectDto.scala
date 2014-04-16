package uk.co.morleydev.zander.client.model.net

import com.fasterxml.jackson.annotation.JsonProperty

case class ProjectDto(@JsonProperty("git") git : String) {
}
