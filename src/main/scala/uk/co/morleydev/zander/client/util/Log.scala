package uk.co.morleydev.zander.client.util

object Log {
  def message(v1 : Any) = println(v1)
  def warning(v1 : Any) = println("%s[WARN] %s%s".format(Console.YELLOW, v1, Console.RESET))
  def error(v1 : Any) = println("%s[ERR] %s%s".format(Console.RED, v1, Console.RESET))
}
