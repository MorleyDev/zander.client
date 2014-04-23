package uk.co.morleydev.zander.client.test.unit.data.program

import java.io.File
import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.data.BuildModeBuildTypeMap
import uk.co.morleydev.zander.client.data.exception.CMakeBuildFailedException
import uk.co.morleydev.zander.client.data.program.{ProgramRunner, CMakeBuildCachedSource}
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.test.gen.{GenNative, GenModel}
import uk.co.morleydev.zander.client.test.unit.UnitTest

class CMakeBuildCachedSourceTests extends UnitTest {

  describe("Given a CMakeBuildCachedSource") {
    describe("When invoking with a project, compiler and debug build mode") {
      val cmake = "cmake"
      val mockProgramRunner = mock[ProgramRunner]
      Mockito.when(mockProgramRunner.apply(Matchers.any[Seq[String]], Matchers.any[File]))
        .thenReturn(0)

      val tmpFilePath = new File("./tmp/path/place")
      val expectedBuildType = GenNative.genAlphaNumericString(1, 20)

      val mockBuildModeType = mock[BuildModeBuildTypeMap]
      Mockito.when(mockBuildModeType.apply(Matchers.any[BuildMode]))
        .thenReturn(expectedBuildType)

      val cmakeBuildLocal = new CMakeBuildCachedSource(cmake, mockProgramRunner, tmpFilePath, mockBuildModeType)

      val mode = GenModel.arg.genBuildMode()
      cmakeBuildLocal.apply(GenModel.arg.genProject(), GenModel.arg.genCompiler(), mode)

      it("Then the build type is retrieved") {
        Mockito.verify(mockBuildModeType).apply(mode)
      }
      it("Then the cmake build is invoked") {
        Mockito.verify(mockProgramRunner).apply(Seq[String](cmake, "--build", ".", "--config", expectedBuildType), tmpFilePath)
      }
    }
  }

  describe("When invoking with a project, compiler and build mode fails with a non-zero exit code") {
    val cmake = "cmake"
    val mockProgramRunner = mock[ProgramRunner]
    Mockito.when(mockProgramRunner.apply(Matchers.any[Seq[String]], Matchers.any[File]))
      .thenReturn(GenNative.genIntExcluding(-1000, 1000, Seq[Int](0)))

    val tmpFilePath = new File("./tmp/path/place")

    val mockBuildModeBuildTypeMap = mock[BuildModeBuildTypeMap]
    Mockito.when(mockBuildModeBuildTypeMap.apply(Matchers.any[BuildMode])).thenReturn(GenNative.genAsciiString(1, 20))

    val cmakeBuildLocal = new CMakeBuildCachedSource(cmake, mockProgramRunner, tmpFilePath, mockBuildModeBuildTypeMap)

    val thrownException: Throwable = try {
      cmakeBuildLocal.apply(GenModel.arg.genProject(), GenModel.arg.genCompiler(), GenModel.arg.genBuildMode())
      null
    } catch {
      case e: Throwable => e
    }
    it("Then an exception was thrown") {
      assert(thrownException != null)
    }
    it("Then the expected exception was thrown") {
      assert(thrownException.isInstanceOf[CMakeBuildFailedException])
    }
  }
}
