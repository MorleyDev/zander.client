package uk.co.morleydev.zander.client.test.spec

import org.scalatest.FunSpec
import org.scalatest.mock.MockitoSugar
import uk.co.morleydev.zander.client.test.spec.util.RealTestHarness

abstract class SpecificationTest extends FunSpec with MockitoSugar {

  def start() : RealTestHarness =
    new RealTestHarness(this)

  def it(desc : String)(testFunc: => Unit) : Unit = {
    it(desc, SpecificationTag)(testFunc)
  }

  def cmakeTestCase(compiler : String, mode: String, cmakeBuildType: String, generator: String) {
    throw new NotImplementedError()
  }

  def noBuildTestCase(compiler : String, mode: String) {
    throw new NotImplementedError()
  }

  def runAllTestCmakeCases() {
    def testFor(compiler : String, generator : String) {
      cmakeTestCase(compiler, "debug", "Debug", generator)
      cmakeTestCase(compiler, "release", "Release", generator)
    }

    testFor("gnu", "-G\"MinGW Makefiles\"")
    testFor("msvc10", "-G\"Visual Studio 10\"")
    testFor("msvc11", "-G\"Visual Studio 11\"")
    testFor("msvc12", "-G\"Visual Studio 12\"")
    testFor("msvc10w64", "-G\"Visual Studio 10 Win64\"")
    testFor("msvc11w64", "-G\"Visual Studio 11 Win64\"")
    testFor("msvc12w64", "-G\"Visual Studio 12 Win64\"")
  }

  def runAllTestNoBuildCases() {
    def testFor(compiler : String) {
      noBuildTestCase(compiler, "debug")
      noBuildTestCase(compiler, "release")
    }

    testFor("gnu")
    testFor("msvc10")
    testFor("msvc11")
    testFor("msvc12")
  }
}
