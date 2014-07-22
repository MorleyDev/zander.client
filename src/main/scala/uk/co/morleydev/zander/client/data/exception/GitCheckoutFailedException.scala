package uk.co.morleydev.zander.client.data.exception

class GitCheckoutFailedException(val exitCode : Int) extends RuntimeException {
  override def getMessage: String = "Git Checkout failed with exitCode " + exitCode
}
