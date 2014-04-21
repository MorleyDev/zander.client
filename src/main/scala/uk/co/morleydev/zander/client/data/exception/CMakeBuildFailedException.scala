package uk.co.morleydev.zander.client.data.exception

class CMakeBuildFailedException(val exitCode : Int) extends RuntimeException {
  override def getMessage: String = "CMake Build failed with exitCode " + exitCode
}


