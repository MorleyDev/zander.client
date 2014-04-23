package uk.co.morleydev.zander.client.test.unit

import org.scalatest.FunSpec
import org.scalatest.mock.MockitoSugar

abstract class UnitTest extends FunSpec with MockitoSugar {

  def it(desc : String)(testFunc: => Unit) : Unit = {
    it(desc, UnitTag)(testFunc)
  }
}
