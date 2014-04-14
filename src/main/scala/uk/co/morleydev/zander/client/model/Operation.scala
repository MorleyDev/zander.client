package uk.co.morleydev.zander.client.model

object Operation extends Enumeration {

  type Operation = Value

  val Install = Value("install")
  val Get = Value("get")
  val Purge = Value("purge")
  val Update = Value("update")
}
