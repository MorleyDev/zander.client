package uk.co.morleydev.zander.client.test.unit.data.map

import uk.co.morleydev.zander.client.data.map.CMakeCompilerGeneratorMap
import uk.co.morleydev.zander.client.model.arg.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.test.unit.UnitTest

class CMakeCompilerGeneratorMapTests extends UnitTest {

  private def testCase(compiler : BuildCompiler, generator : Seq[String]) {

    describe("Given a Compiler Generator map") {
      describe("When mapping %s to %s".format(compiler, generator)) {
        val result = CMakeCompilerGeneratorMap(compiler)
        it("Then the expected result is returned") {
          assert(result == generator)
        }
      }
    }
  }

  testCase(BuildCompiler.Mingw, Seq[String]("-G\"MinGW", "Makefiles\""))
  testCase(BuildCompiler.Unix, Seq[String]("-G\"Unix", "Makefiles\""))
  testCase(BuildCompiler.Msys, Seq[String]("-G\"MSYS", "Makefiles\""))
  testCase(BuildCompiler.Borland, Seq[String]("-G\"Borland", "Makefiles\""))
  testCase(BuildCompiler.NMake, Seq[String]("-G\"NMake", "Makefiles\""))
  testCase(BuildCompiler.NMakeJom, Seq[String]("-G\"NMake", "Makefiles", "JOM\""))

  testCase(BuildCompiler.VisualStudio10, Seq[String]("-G\"Visual", "Studio", "10\""))
  testCase(BuildCompiler.VisualStudio11, Seq[String]("-G\"Visual", "Studio", "11\""))
  testCase(BuildCompiler.VisualStudio12, Seq[String]("-G\"Visual", "Studio", "12\""))

  testCase(BuildCompiler.VisualStudio10Win64, Seq[String]("-G\"Visual", "Studio", "10", "Win64\""))
  testCase(BuildCompiler.VisualStudio11Win64, Seq[String]("-G\"Visual", "Studio", "11", "Win64\""))
  testCase(BuildCompiler.VisualStudio12Win64, Seq[String]("-G\"Visual", "Studio", "12", "Win64\""))
}
