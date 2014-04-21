package uk.co.morleydev.zander.client.test.unit.data.map

import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.data.map.CmakeCompilerGeneratorMap
import uk.co.morleydev.zander.client.model.arg.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler

class CmakeCompilerGeneratorMapTests extends FunSpec {

  private def testCase(compiler : BuildCompiler, generator : Seq[String]) {

    describe("Given a Compiler Generator map") {
      describe("When mapping %s to %s".format(compiler, generator)) {
        val result = CmakeCompilerGeneratorMap(compiler)
        it("Then the expected result is returned") {
          assert(result == generator)
        }
      }
    }
  }
  testCase(BuildCompiler.GnuCxx, Seq[String]("-G\"MinGW", "Makefiles\""))
  testCase(BuildCompiler.VisualStudio10, Seq[String]("-G\"Visual", "Studio", "10\""))
  testCase(BuildCompiler.VisualStudio11, Seq[String]("-G\"Visual", "Studio", "11\""))
  testCase(BuildCompiler.VisualStudio12, Seq[String]("-G\"Visual", "Studio", "12\""))
}
