package uk.co.morleydev.zander.client.unit.data.program

import uk.co.morleydev.zander.client.data.program.{CMakeInstallLocal, ProgramRunner}
import java.io.File
import uk.co.morleydev.zander.client.gen.GenModel
import org.mockito.Mockito
import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSpec

class CMakeInstallLocalTests extends FunSpec with MockitoSugar {

  describe("Given a CMakeInstallLocal") {

    val cmake = "cmake"
    val mockProgramRunner = mock[ProgramRunner]
    val tmpFilePath = new File("./tmp/path/place")

    val cmakeInstallLocal = new CMakeInstallLocal(cmake, mockProgramRunner, tmpFilePath)

    describe("When invoking with a project, compiler and build mode") {

      cmakeInstallLocal.apply(GenModel.arg.genProject(), GenModel.arg.genCompiler())

      it("Then the cmake install is invoked") {
        Mockito.verify(mockProgramRunner).apply(Seq[String](cmake, "--build", ".", "--", "install"), tmpFilePath)
      }
    }
  }

}
