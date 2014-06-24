package uk.co.morleydev.zander.client.test.spec

import uk.co.morleydev.zander.client.test.spec.util.RealTestHarness
import uk.co.morleydev.zander.client.test.util.AbstractTest

abstract class SpecTest extends AbstractTest(SpecificationTag) {

  def start() : RealTestHarness =
    new RealTestHarness(this)

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
    "unix" -> "Unix Makefiles",
    "mingw" -> "MinGW Makefiles",
    "msys" -> "MSYS Makefiles",
    "borland" -> "Borland Makefiles",
    "nmake" -> "NMake Makefiles",
    "nmake-jom" -> "NMake Makefiles JOM",
    "msvc10" -> "Visual Studio 10",
    "msvc11" -> "Visual Studio 11",
    "msvc12" -> "Visual Studio 12",
    "msvc10w64" -> "Visual Studio 10 Win64",
    "msvc11w64" -> "Visual Studio 11 Win64",
    "msvc12w64" -> "Visual Studio 12 Win64"
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
