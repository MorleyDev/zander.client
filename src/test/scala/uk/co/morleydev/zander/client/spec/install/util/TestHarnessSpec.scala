package uk.co.morleydev.zander.client.spec.install.util

import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSpec

abstract class TestHarnessSpec extends FunSpec with MockitoSugar {

  def start() : RealTestHarness =
    new RealTestHarness(this)

  def _it(desc : String)(testFunc: => Unit) : Unit = {
    it(desc)(testFunc)
  }
}
