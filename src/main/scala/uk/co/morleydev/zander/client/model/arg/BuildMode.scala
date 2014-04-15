package uk.co.morleydev.zander.client.model.arg

object BuildMode extends Enumeration {

  type BuildMode = Value

  val Debug = Value("debug")
  val Release = Value("release")
}