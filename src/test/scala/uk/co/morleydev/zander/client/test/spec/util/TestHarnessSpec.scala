package uk.co.morleydev.zander.client.test.spec.util

import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSpec

abstract class TestHarnessSpec extends FunSpec with MockitoSugar {

  def start() : RealTestHarness =
    new RealTestHarness(this)

  def _it(desc : String)(testFunc: => Unit) : Unit = {
    it(desc)(testFunc)
  }

  def cmakeTestCase(compiler : String, mode: String, cmakeBuildType: String, generator: String) {
    throw new NotImplementedError()
  }

  def noBuildTestCase(compiler : String, mode: String) {
    throw new NotImplementedError()
  }

  def runAllTestCmakeCases() {
    cmakeTestCase("gnu", "debug", "Debug", "-G\"MinGW Makefiles\"")
    cmakeTestCase("gnu", "release", "Release", "-G\"MinGW Makefiles\"")

    cmakeTestCase("msvc10", "debug", "Debug", "-G\"Visual Studio 10\"")
    cmakeTestCase("msvc10", "release", "Release", "-G\"Visual Studio 10\"")

    cmakeTestCase("msvc11", "debug", "Debug", "-G\"Visual Studio 11\"")
    cmakeTestCase("msvc11", "release", "Release", "-G\"Visual Studio 11\"")

    cmakeTestCase("msvc12", "debug", "Debug", "-G\"Visual Studio 12\"")
    cmakeTestCase("msvc12", "release", "Release", "-G\"Visual Studio 12\"")
  }

  def runAllTestNoBuildCases() {
    noBuildTestCase("gnu", "debug")
    noBuildTestCase("gnu", "release")
    noBuildTestCase("msvc10", "debug")
    noBuildTestCase("msvc10", "release")
    noBuildTestCase("msvc11", "debug")
    noBuildTestCase("msvc11", "release")
    noBuildTestCase("msvc12", "debug")
    noBuildTestCase("msvc12", "release")
  }
}
