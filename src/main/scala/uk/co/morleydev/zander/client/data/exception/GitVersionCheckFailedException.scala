package uk.co.morleydev.zander.client.data.exception

class GitVersionCheckFailedException(val exitCode : Int) extends RuntimeException {
  override def getMessage: String = "Git version Check failed with exitCode " + exitCode
}
