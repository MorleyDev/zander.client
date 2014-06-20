package uk.co.morleydev.zander.client.test.unit.data.map

import uk.co.morleydev.zander.client.data.map.CMakeCompilerGeneratorMap
import uk.co.morleydev.zander.client.model.arg.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.test.unit.UnitTest

class CMakeCompilerGeneratorMapTests extends UnitTest {

  private def testCase(compiler : BuildCompiler, generator : String) {
    describe("Given a Compiler Generator map for Windows") {
      describe("When mapping %s to %s".format(compiler, generator)) {
        val result = new CMakeCompilerGeneratorMap(isWindows = true)(compiler)
        it("Then the expected result is returned") {
          assert(result == ("-G\"" + generator + "\"").split(' ').toSeq)
        }
      }
    }
    describe("Given a Compiler Generator map not for Windows") {
      describe("When mapping %s to %s".format(compiler, generator)) {
        val result = new CMakeCompilerGeneratorMap(isWindows = false)(compiler)
        it("Then the expected result is returned") {
          assert(result == Seq("-G", generator))
        }
      }
    }
  }

  testCase(BuildCompiler.Mingw, "MinGW Makefiles")
  testCase(BuildCompiler.Unix, "Unix Makefiles")
  testCase(BuildCompiler.Msys, "MSYS Makefiles")
  testCase(BuildCompiler.Borland, "Borland Makefiles")
  testCase(BuildCompiler.NMake, "NMake Makefiles")
  testCase(BuildCompiler.NMakeJom, "NMake Makefiles JOM")

  testCase(BuildCompiler.VisualStudio10, "Visual Studio 10")
  testCase(BuildCompiler.VisualStudio11, "Visual Studio 11")
  testCase(BuildCompiler.VisualStudio12, "Visual Studio 12")

  testCase(BuildCompiler.VisualStudio10Win64, "Visual Studio 10 Win64")
  testCase(BuildCompiler.VisualStudio11Win64, "Visual Studio 11 Win64")
  testCase(BuildCompiler.VisualStudio12Win64, "Visual Studio 12 Win64")
}
