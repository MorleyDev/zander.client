package uk.co.morleydev.zander.client.data

import java.io.File

trait NativeProcessBuilder {
  def directory(directory : File) : NativeProcessBuilder
  def start() : Process
}
