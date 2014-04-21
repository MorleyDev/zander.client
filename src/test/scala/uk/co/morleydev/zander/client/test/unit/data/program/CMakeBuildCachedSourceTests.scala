package uk.co.morleydev.zander.client.test.unit.data.program

import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.data.program.{ProgramRunner, CMakeBuildCachedSource}
import java.io.File
import uk.co.morleydev.zander.client.test.gen.GenModel
import org.mockito.Mockito
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.BuildMode

class CMakeBuildCachedSourceTests extends FunSpec with MockitoSugar {

  describe("Given a CMakeBuildLocal") {
    describe("When invoking with a project, compiler and debug build mode") {
      val cmake = "cmake"
      val mockProgramRunner = mock[ProgramRunner]
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
      val tmpFilePath = new File("./tmp/path/place")
      val cmakeBuildLocal = new CMakeBuildCachedSource(cmake, mockProgramRunner, tmpFilePath)

      val mode = BuildMode.Release
      cmakeBuildLocal.apply(GenModel.arg.genProject(), GenModel.arg.genCompiler(), mode)

      it("Then the cmake build is invoked") {
        Mockito.verify(mockProgramRunner).apply(Seq[String](cmake, "--build", ".", "--config", "Release"), tmpFilePath)
      }
    }
  }
}
