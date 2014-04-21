package uk.co.morleydev.zander.client.test.unit.data.program

import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.data.program.{ProgramRunner, CMakeBuildCachedSource}
import java.io.File
import uk.co.morleydev.zander.client.test.gen.{GenNative, GenModel}
import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.BuildMode
import uk.co.morleydev.zander.client.data.exception.CMakeBuildFailedException

class CMakeBuildCachedSourceTests extends FunSpec with MockitoSugar {

  describe("Given a CMakeBuildCachedSource") {
    describe("When invoking with a project, compiler and debug build mode") {
      val cmake = "cmake"
      val mockProgramRunner = mock[ProgramRunner]
      Mockito.when(mockProgramRunner.apply(Matchers.any[Seq[String]], Matchers.any[File]))
        .thenReturn(0)

      val tmpFilePath = new File("./tmp/path/place")
      val cmakeBuildLocal = new CMakeBuildCachedSource(cmake, mockProgramRunner, tmpFilePath)

      val mode = BuildMode.Debug
      cmakeBuildLocal.apply(GenModel.arg.genProject(), GenModel.arg.genCompiler(), mode)

      it("Then the cmake build is invoked") {
        Mockito.verify(mockProgramRunner).apply(Seq[String](cmake, "--build", ".", "--config", "Debug"), tmpFilePath)
      }
    }

    describe("When invoking with a project, compiler and release build mode") {
      val cmake = "cmake"
      val mockProgramRunner = mock[ProgramRunner]
      Mockito.when(mockProgramRunner.apply(Matchers.any[Seq[String]], Matchers.any[File]))
        .thenReturn(0)

      val tmpFilePath = new File("./tmp/path/place")
      val cmakeBuildLocal = new CMakeBuildCachedSource(cmake, mockProgramRunner, tmpFilePath)

      val mode = BuildMode.Release
      cmakeBuildLocal.apply(GenModel.arg.genProject(), GenModel.arg.genCompiler(), mode)

      it("Then the cmake build is invoked") {
        Mockito.verify(mockProgramRunner).apply(Seq[String](cmake, "--build", ".", "--config", "Release"), tmpFilePath)
      }
    }

    describe("When invoking with a project, compiler and build mode fails with a non-zero exit code") {
      val cmake = "cmake"
      val mockProgramRunner = mock[ProgramRunner]
      Mockito.when(mockProgramRunner.apply(Matchers.any[Seq[String]], Matchers.any[File]))
             .thenReturn(GenNative.genIntExcluding(-1000, 1000, Seq[Int](0)))

      val tmpFilePath = new File("./tmp/path/place")
      val cmakeBuildLocal = new CMakeBuildCachedSource(cmake, mockProgramRunner, tmpFilePath)

      val thrownException : Throwable = try {
        cmakeBuildLocal.apply(GenModel.arg.genProject(), GenModel.arg.genCompiler(), GenModel.arg.genBuildMode())
        null
      } catch {
        case e : Throwable => e
      }
      it("Then an exception was thrown") {
        assert(thrownException != null)
      }
      it("Then the expected exception was thrown") {
        assert(thrownException.isInstanceOf[CMakeBuildFailedException])
      }
    }
  }
}
