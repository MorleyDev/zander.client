package uk.co.morleydev.zander.client.util

object Log {
  def message(v1 : Any) = println(v1)
  def warning(v1 : Any) = println(Console.YELLOW + "[WARN] " + v1 + Console.RESET)
  def error(v1 : Any) = println(Console.RED + "[ERR] " + Console.RESET)
}
