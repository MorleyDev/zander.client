package uk.co.morleydev.zander.client.unit.data.program

import org.scalatest.FunSpec
import org.scalatest.mock.MockitoSugar
import uk.co.morleydev.zander.client.data.program.{ProgramRunner, CMakePrebuildCachedSource}
import uk.co.morleydev.zander.client.gen.{GenNative, GenModel}
import org.mockito.Mockito
import java.io.File
import uk.co.morleydev.zander.client.model.arg.Compiler.Compiler
import uk.co.morleydev.zander.client.model.arg.Compiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.BuildMode

class CMakePrebuildLocalTests extends FunSpec with MockitoSugar {

  def testCase(compiler: Compiler, mode: BuildMode, buildType: String, cachePath: String) = {

    describe("Given a CMakePrebuildLocal") {

      val mockProgramRunner = mock[ProgramRunner]

      val cmake = GenNative.genAlphaNumericString(3, 10)
      val cache = new File("./cache/path")
      val tempPath = new File("./tmp/adsafaw")
      val cmakePrebuildLocal = new CMakePrebuildCachedSource(cmake, mockProgramRunner, cache, tempPath)

      describe("When applied for a project on the " + compiler + " compiler and " + mode + " build") {
        val project = GenModel.arg.genProject()
        cmakePrebuildLocal.apply(project, compiler, mode)

        it("Then the cmake program was ran with the expected arguments in the expected directory") {
          val expectedArguments = Seq[String](cmake,
            new File(cache, project.value + "/source").getAbsolutePath,
            "-G\"MinGW", "Makefiles\"",
            "-DCMAKE_BUILD_TYPE=" + buildType,
            "-DCMAKE_INSTALL_PREFIX=" + new File(cache, project.value + "/" + cachePath).getAbsolutePath)

          Mockito.verify(mockProgramRunner).apply(expectedArguments, tempPath)
        }
      }
    }
  }
  testCase(Compiler.GnuCxx, BuildMode.Debug, "Debug", "gnu.debug")
  testCase(Compiler.GnuCxx, BuildMode.Release, "Release", "gnu.release")
}
