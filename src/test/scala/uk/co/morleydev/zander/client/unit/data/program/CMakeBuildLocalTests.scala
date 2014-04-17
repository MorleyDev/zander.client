package uk.co.morleydev.zander.client.unit.data.program

import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.data.program.{ProgramRunner, CMakeBuildLocal}
import java.io.File
import uk.co.morleydev.zander.client.gen.GenModel
import org.mockito.Mockito

class CMakeBuildLocalTests extends FunSpec with MockitoSugar {

  describe("Given a CMakeBuildLocal") {

    val cmake = "cmake"
    val mockProgramRunner = mock[ProgramRunner]
    val tmpFilePath = new File("./tmp/path/place")

    val cmakeBuildLocal = new CMakeBuildLocal(cmake, mockProgramRunner, tmpFilePath)

    describe("When invoking with a project, compiler and build mode") {

      cmakeBuildLocal.apply(GenModel.arg.genProject(), GenModel.arg.genCompiler(), GenModel.arg.genBuildMode())

      it("Then the cmake build is invoked") {
        Mockito.verify(mockProgramRunner).apply(Seq[String](cmake, "--build", "."), tmpFilePath)
      }
    }
  }

}
