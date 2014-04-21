package uk.co.morleydev.zander.client.test.unit.data.program

import org.scalatest.FunSpec
import org.scalatest.mock.MockitoSugar
import uk.co.morleydev.zander.client.data.program.{ProgramRunner, CMakePrebuildCachedSource}
import uk.co.morleydev.zander.client.test.gen.{GenNative, GenModel}
import org.mockito.{Matchers, Mockito}
import java.io.File
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.BuildMode
import uk.co.morleydev.zander.client.data.CompilerGeneratorMap

class CMakePrebuildCachedSourceTests extends FunSpec with MockitoSugar {

  def testCase(compiler: BuildCompiler, mode: BuildMode, buildType: String, cachePath: String) = {

    describe("Given a CMakePrebuildLocal") {

      val mockProgramRunner = mock[ProgramRunner]

      val cmake = GenNative.genAlphaNumericString(3, 10)
      val cache = new File("./cache/path")
      val tempPath = new File("./tmp/adsafaw")
      val mockCompilerGeneratorMap = mock[CompilerGeneratorMap]

      val cmakePrebuildLocal = new CMakePrebuildCachedSource(cmake,
        mockProgramRunner,
        cache,
        tempPath,
        mockCompilerGeneratorMap)

      describe("When applied for a project on the " + compiler + " compiler and " + mode + " build") {
        val expectedGenerator = GenNative.genSequence(1, 10, () => GenNative.genAlphaNumericString(1, 10))
        Mockito.when(mockCompilerGeneratorMap.apply(Matchers.any[BuildCompiler]))
               .thenReturn(expectedGenerator)

        val project = GenModel.arg.genProject()
        cmakePrebuildLocal.apply(project, compiler, mode)

        it("Then the generator for the compiler is acquired") {
          Mockito.verify(mockCompilerGeneratorMap).apply(compiler)
        }
        it("Then the cmake program was ran with the expected arguments in the expected directory") {
          val expectedArguments =
            Seq[String](cmake, new File(cache, project.value + "/source").getAbsolutePath) ++
              expectedGenerator ++
            Seq[String]("-DCMAKE_BUILD_TYPE=" + buildType,
                        "-DCMAKE_INSTALL_PREFIX=" + new File(cache, project.value + "/" + cachePath).getAbsolutePath)

          Mockito.verify(mockProgramRunner).apply(expectedArguments, tempPath)
        }
      }
    }
  }

  testCase(BuildCompiler.GnuCxx, BuildMode.Debug, "Debug", "gnu.debug")
  testCase(BuildCompiler.GnuCxx, BuildMode.Release, "Release", "gnu.release")
  testCase(BuildCompiler.VisualStudio10, BuildMode.Debug, "Debug", "msvc10.debug")
  testCase(BuildCompiler.VisualStudio10, BuildMode.Release, "Release", "msvc10.release")
  testCase(BuildCompiler.VisualStudio11, BuildMode.Debug, "Debug", "msvc11.debug")
  testCase(BuildCompiler.VisualStudio11, BuildMode.Release, "Release", "msvc11.release")
  testCase(BuildCompiler.VisualStudio12, BuildMode.Debug, "Debug", "msvc12.debug")
  testCase(BuildCompiler.VisualStudio12, BuildMode.Release, "Release", "msvc12.release")
}
