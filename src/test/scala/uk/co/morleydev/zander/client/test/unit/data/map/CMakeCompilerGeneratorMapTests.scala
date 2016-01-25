package uk.co.morleydev.zander.client.test.unit.data.map

import uk.co.morleydev.zander.client.data.map.CMakeCompilerGeneratorMap
import uk.co.morleydev.zander.client.model.arg.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.test.unit.UnitTest

class CMakeCompilerGeneratorMapTests extends UnitTest {

  private def whenMappingXtoYThenExpectedResultIsReturned(compiler : BuildCompiler, generator : String) {
      describe("When mapping %s to %s".format(compiler, generator)) {
        val result = CMakeCompilerGeneratorMap(compiler)
        it("Then the expected result is returned") {
          assert(result == Seq("-G", generator))
        }
      }
  }

  describe("Given a Compiler Generator map") {
    whenMappingXtoYThenExpectedResultIsReturned(BuildCompiler.Mingw, "MinGW Makefiles")
    whenMappingXtoYThenExpectedResultIsReturned(BuildCompiler.Unix, "Unix Makefiles")
    whenMappingXtoYThenExpectedResultIsReturned(BuildCompiler.Msys, "MSYS Makefiles")
    whenMappingXtoYThenExpectedResultIsReturned(BuildCompiler.Borland, "Borland Makefiles")
    whenMappingXtoYThenExpectedResultIsReturned(BuildCompiler.NMake, "NMake Makefiles")
    whenMappingXtoYThenExpectedResultIsReturned(BuildCompiler.NMakeJom, "NMake Makefiles JOM")
    whenMappingXtoYThenExpectedResultIsReturned(BuildCompiler.VisualStudio10, "Visual Studio 10")
    whenMappingXtoYThenExpectedResultIsReturned(BuildCompiler.VisualStudio11, "Visual Studio 11")
    whenMappingXtoYThenExpectedResultIsReturned(BuildCompiler.VisualStudio12, "Visual Studio 12")
    whenMappingXtoYThenExpectedResultIsReturned(BuildCompiler.VisualStudio14, "Visual Studio 14")
    whenMappingXtoYThenExpectedResultIsReturned(BuildCompiler.VisualStudio10Win64, "Visual Studio 10 Win64")
    whenMappingXtoYThenExpectedResultIsReturned(BuildCompiler.VisualStudio11Win64, "Visual Studio 11 Win64")
    whenMappingXtoYThenExpectedResultIsReturned(BuildCompiler.VisualStudio12Win64, "Visual Studio 12 Win64")
    whenMappingXtoYThenExpectedResultIsReturned(BuildCompiler.VisualStudio14Win64, "Visual Studio 14 Win64")
  }
}
