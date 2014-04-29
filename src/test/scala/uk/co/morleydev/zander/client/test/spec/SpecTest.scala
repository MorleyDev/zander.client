package uk.co.morleydev.zander.client.test.spec

import org.scalatest.FunSpec
import org.scalatest.mock.MockitoSugar
import uk.co.morleydev.zander.client.test.spec.util.RealTestHarness

abstract class SpecTest extends FunSpec with MockitoSugar {

  def start() : RealTestHarness =
    new RealTestHarness(this)

  def it(desc : String)(testFunc: => Unit) : Unit = {
    it(desc, SpecificationTag)(testFunc)
  }

  def _assert(value : Boolean, hint : String) : Unit = {
    assert(value, hint)
  }

  def cmakeTestCase(compiler : String, mode: String, cmakeBuildType: String, generator: String) {
    throw new NotImplementedError()
  }

  def noBuildTestCase(compiler : String, mode: String) {
    throw new NotImplementedError()
  }

  private val testCaseMap = Map[String, String](
    "unix" -> "-G\"Unix Makefiles\"",
    "mingw" -> "-G\"MinGW Makefiles\"",
    "msys" -> "-G\"MSYS Makefiles\"",
    "borland" -> "-G\"Borland Makefiles\"",
    "nmake" -> "-G\"NMake Makefiles\"",
    "nmake-jom" -> "-G\"NMake Makefiles JOM\"",
    "msvc10" -> "-G\"Visual Studio 10\"",
    "msvc11" -> "-G\"Visual Studio 11\"",
    "msvc12" -> "-G\"Visual Studio 12\"",
    "msvc10w64" -> "-G\"Visual Studio 10 Win64\"",
    "msvc11w64" -> "-G\"Visual Studio 11 Win64\"",
    "msvc12w64" -> "-G\"Visual Studio 12 Win64\""
  )

  def runAllTestCmakeCases() {
    def testFor(compiler : String, generator : String) {
      cmakeTestCase(compiler, "debug", "Debug", generator)
      cmakeTestCase(compiler, "release", "Release", generator)
    }

    testCaseMap.foreach(pair => {
      testFor(pair._1, pair._2)
    })
  }

  def runAllTestNoBuildCases() {
    def testFor(compiler : String) {
      noBuildTestCase(compiler, "debug")
      noBuildTestCase(compiler, "release")
    }

    testCaseMap.foreach(pair => testFor(pair._1))
  }
}
