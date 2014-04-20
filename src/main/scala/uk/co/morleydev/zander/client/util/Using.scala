package uk.co.morleydev.zander.client.util

object Using {
  def using[A, B <: { def close() : Unit }] (closeable: B) (f: B => A): A = {
    try {
      f(closeable)
    } finally {
      closeable.close()
    }
  }

  def using[A, B <: { def close() : Unit }, C <: { def close() : Unit }] (c1: B, c2 : C) (f : (B, C) => A): A = {
    try {
      f(c1, c2)
    } finally {
      c1.close()
      c2.close()
    }
  }

  def using[A,
  B <: { def close() : Unit },
  C <: { def close() : Unit },
  D <: { def close() : Unit }] (c1: B, c2 : C, c3 : D) (f : (B, C, D) => A): A = {
    try {
      f(c1, c2, c3)
    } finally {
      c1.close()
      c2.close()
      c3.close()
    }
  }
}
