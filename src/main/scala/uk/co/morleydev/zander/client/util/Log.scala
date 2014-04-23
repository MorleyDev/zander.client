package uk.co.morleydev.zander.client.util

object Log {
  trait LogTrait {
    def message(v1 : Any)
    def warning(v1 : Any)
    def error(v1 : Any)
  }

  object LogConsoleImpl extends LogTrait {
    override def message(v1 : Any) = println(v1)
    override def warning(v1 : Any) = println("%s[WARN] %s%s".format(Console.YELLOW, v1, Console.RESET))
    override def error(v1 : Any) = println("%s[ERR] %s%s".format(Console.RED, v1, Console.RESET))
  }

  object LogDisableImpl extends LogTrait {
    override def message(v1 : Any) = { }
    override def warning(v1 : Any) = { }
    override def error(v1 : Any) = { }
  }

  private var logImpl : LogTrait = LogDisableImpl

  def enableLogging() { logImpl = LogConsoleImpl }
  def disableLogging() { logImpl = LogDisableImpl }

  def message(v1 : Any) = logImpl.message(v1)
  def warning(v1 : Any) = logImpl.warning(v1)
  def error(v1 : Any) = logImpl.error(v1)
}
