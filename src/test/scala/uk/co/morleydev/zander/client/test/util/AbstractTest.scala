package uk.co.morleydev.zander.client.test.util

import org.scalatest.mock.MockitoSugar
import org.scalatest.{FunSpec, Tag}

class AbstractTest(tag : Tag) extends FunSpec with MockitoSugar {

  def it(desc : String)(testFunc: => Unit) : Unit = {

    val error = try {
      testFunc
      None
    } catch {
      case e : Throwable => Some(e)
    }

    it(desc, tag) {
      if (error.isDefined)
        throw error.get
    }
  }
}
