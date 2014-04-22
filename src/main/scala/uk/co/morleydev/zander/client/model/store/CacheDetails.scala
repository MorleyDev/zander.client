package uk.co.morleydev.zander.client.model.store

import com.fasterxml.jackson.annotation.JsonProperty

case class CacheDetails(@JsonProperty("version") version : String)

