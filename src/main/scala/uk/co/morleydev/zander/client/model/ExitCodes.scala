package uk.co.morleydev.zander.client.model

object ExitCodes {
  val Success = 0
  val InvalidOperation = 1
  val InvalidProject = 2
  val InvalidCompiler = 3
  val InvalidBuildMode = 4
  val InvalidArgumentCount = 5
  val ArtefactsAlreadyInstalled = 10
  val ArtefactsNotInstalled = 11
  val EndpointNotFound = -404
  val UnknownError = 500
}
