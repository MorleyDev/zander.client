package uk.co.morleydev.zander.client.util

object Using {
  def using[A, B <: {def close(): Unit}] (closeable: B) (f: B => A): A = {
    try {
      f(closeable)
    } finally {
      if (closeable != null)
        closeable.close()
    }
  }
}
