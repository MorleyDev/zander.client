package uk.co.morleydev.zander.client.data.exception

class CMakeInstallFailedException(val exitCode : Int) extends RuntimeException {
  override def getMessage: String = "CMake Install failed with exitCode " + exitCode
}

