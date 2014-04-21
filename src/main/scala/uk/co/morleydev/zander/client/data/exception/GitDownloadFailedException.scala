package uk.co.morleydev.zander.client.data.exception

class GitDownloadFailedException(val exitCode : Int) extends RuntimeException {
  override def getMessage: String = "Git Download failed with exitCode " + exitCode
}

