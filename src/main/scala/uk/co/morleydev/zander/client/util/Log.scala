package uk.co.morleydev.zander.client.util

object Log {
  def apply(v1 : Any*): Unit = {
    println(v1.mkString(" "))
  }
}
