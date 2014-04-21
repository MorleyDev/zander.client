package uk.co.morleydev.zander.client.data.exception

class CMakePreBuildFailedException(val exitCode : Int) extends RuntimeException {
  override def getMessage: String = "CMake Pre-build failed with exitCode " + exitCode
}
