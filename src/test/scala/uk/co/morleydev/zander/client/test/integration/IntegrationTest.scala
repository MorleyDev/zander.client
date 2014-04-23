package uk.co.morleydev.zander.client.test.integration

import org.scalatest.FunSpec
import org.scalatest.mock.MockitoSugar

abstract class IntegrationTest extends FunSpec with MockitoSugar {

  def it(desc : String)(testFunc: => Unit) : Unit = {
    it(desc, IntegrationTag)(testFunc)
  }
}
