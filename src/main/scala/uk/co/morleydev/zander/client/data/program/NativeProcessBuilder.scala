package uk.co.morleydev.zander.client.data.program

trait NativeProcessBuilder {
  def directory(directory : String) : NativeProcessBuilder
  def start() : Process
}
