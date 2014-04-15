package uk.co.morleydev.zander.client.model.net

import com.fasterxml.jackson.annotation.JsonProperty

case class Project(@JsonProperty("git") git : String) {
}
