package uk.co.morleydev.zander.client.data.program

import java.io.File

trait ProgramRunner extends ((Seq[String], File) => Int) {
  def apply(commands : Seq[String], file : File) : Int
}
