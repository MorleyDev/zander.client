package uk.co.morleydev.zander.client.data.exception

class GitUpdateFailedException(val exitCode : Int) extends RuntimeException {
  override def getMessage: String = "Git Update failed with exitCode " + exitCode
}



