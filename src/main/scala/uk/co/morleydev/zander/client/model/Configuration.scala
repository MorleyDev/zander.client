package uk.co.morleydev.zander.client.model

import com.fasterxml.jackson.annotation.JsonProperty

case class Configuration(
  @JsonProperty("server") server : String) {
}
