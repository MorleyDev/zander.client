package uk.co.morleydev.zander.client.model

object BuildMode extends Enumeration {

  type BuildMode = Value

  val Debug = Value("debug")
  val Release = Value("release")
}