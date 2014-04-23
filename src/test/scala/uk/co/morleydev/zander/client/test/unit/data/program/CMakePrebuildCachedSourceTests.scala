package uk.co.morleydev.zander.client.test.unit.data.program

import java.io.File
import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.data.exception.CMakePreBuildFailedException
import uk.co.morleydev.zander.client.data.program.{ProgramRunner, CMakePreBuildCachedSource}
import uk.co.morleydev.zander.client.data.{BuildModeBuildTypeMap, CompilerGeneratorMap}
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.test.gen.{GenNative, GenModel}
import uk.co.morleydev.zander.client.test.unit.UnitTest

class CMakePrebuildCachedSourceTests extends UnitTest {

  describe("Given a CMakePrebuildLocal") {

    val mockProgramRunner = mock[ProgramRunner]

    val cmake = GenNative.genAlphaNumericString(3, 10)
    val cache = new File("./cache/path")
    val tempPath = new File("./tmp/adsafaw")
    val mockCompilerGeneratorMap = mock[CompilerGeneratorMap]

    val mockBuildTypeMap = mock[BuildModeBuildTypeMap]
    val buildType = GenNative.genAlphaNumericString(1, 20)
    Mockito.when(mockBuildTypeMap.apply(Matchers.any[BuildMode]))
      .thenReturn(buildType)

    val cmakePrebuildLocal = new CMakePreBuildCachedSource(cmake,
      mockProgramRunner,
      cache,
      tempPath,
      mockCompilerGeneratorMap,
      mockBuildTypeMap)

    describe("When applied for a project") {

      val expectedGenerator = GenNative.genSequence(1, 10, () => GenNative.genAlphaNumericString(1, 10))
      Mockito.when(mockCompilerGeneratorMap.apply(Matchers.any[BuildCompiler]))
        .thenReturn(expectedGenerator)

      Mockito.when(mockProgramRunner.apply(Matchers.any[Seq[String]], Matchers.any[File]))
        .thenReturn(0)

      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()
      cmakePrebuildLocal.apply(project, compiler, mode)

      it("Then the generator for the compiler is acquired") {
        Mockito.verify(mockCompilerGeneratorMap).apply(compiler)
      }
      it("Then the build type for the mode is acquired") {
        Mockito.verify(mockBuildTypeMap).apply(mode)
      }
      it("Then the cmake program was ran with the expected arguments in the expected directory") {
        val expectedArguments =
          Seq[String](cmake, new File(cache, project.value + "/source").getAbsolutePath) ++
            expectedGenerator ++
            Seq[String]("-DCMAKE_BUILD_TYPE=" + buildType,
              "-DCMAKE_INSTALL_PREFIX=" + new File(cache, "%s/%s.%s".format(project, compiler, mode)).getAbsolutePath)

        Mockito.verify(mockProgramRunner).apply(expectedArguments, tempPath)
      }
    }
  }

  describe("Given a CMakePrebuildLocal") {

    val mockProgramRunner = mock[ProgramRunner]
    val mockCompilerGeneratorMap = mock[CompilerGeneratorMap]

    val mockBuildTypeMap = mock[BuildModeBuildTypeMap]
    Mockito.when(mockBuildTypeMap.apply(Matchers.any[BuildMode]))
      .thenReturn(GenNative.genAlphaNumericString(1, 20))

    val cmakePrebuildLocal = new CMakePreBuildCachedSource(GenNative.genAlphaNumericString(3, 10),
      mockProgramRunner,
      new File("./cache/path"),
      new File("./tmp/adsafaw"),
      mockCompilerGeneratorMap,
      mockBuildTypeMap)

    describe("When applied for a project and the prebuild fails") {

      Mockito.when(mockCompilerGeneratorMap.apply(Matchers.any[BuildCompiler]))
        .thenReturn(GenNative.genSequence(1, 10, () => GenNative.genAlphaNumericString(1, 10)))

      Mockito.when(mockProgramRunner.apply(Matchers.any[Seq[String]], Matchers.any[File]))
        .thenReturn(GenNative.genIntExcluding(-1000, 1000, Seq[Int](0)))

      val thrownException: Throwable = try {
        cmakePrebuildLocal.apply(GenModel.arg.genProject(), GenModel.arg.genCompiler(), GenModel.arg.genBuildMode())
        null
      } catch {
        case e: Throwable => e
      }
      it("Then an exception was thrown") {
        assert(thrownException != null)
      }
      it("Then the expected exception was thrown") {
        assert(thrownException.isInstanceOf[CMakePreBuildFailedException])
      }
    }
  }
}
