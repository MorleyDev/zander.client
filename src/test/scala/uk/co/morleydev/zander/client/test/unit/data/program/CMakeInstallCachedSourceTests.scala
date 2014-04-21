package uk.co.morleydev.zander.client.test.unit.data.program

import java.io.File
import org.mockito.Mockito
import org.scalatest.FunSpec
import org.scalatest.mock.MockitoSugar
import uk.co.morleydev.zander.client.data.program.{CMakeInstallCachedSource, ProgramRunner}
import uk.co.morleydev.zander.client.model.arg.BuildMode
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.test.gen.GenModel

class CMakeInstallCachedSourceTests extends FunSpec with MockitoSugar {

  def testCase(mode : BuildMode, config : String) = {
    describe("Given a CMakeInstallLocal") {

      val cmake = "cmake"
      val mockProgramRunner = mock[ProgramRunner]
      val tmpFilePath = new File("./tmp/path/place")

      val cmakeInstallLocal = new CMakeInstallCachedSource(cmake, mockProgramRunner, tmpFilePath)

      describe("When invoking with a project, compiler and "+ mode +" mode") {

        cmakeInstallLocal.apply(GenModel.arg.genProject(), GenModel.arg.genCompiler(), mode)

        it("Then the cmake install is invoked") {
          Mockito.verify(mockProgramRunner).apply(Seq[String](cmake, "--build", ".", "--config", config, "--target", "install"), tmpFilePath)
        }
      }
    }
  }
  testCase(BuildMode.Debug, "Debug")
  testCase(BuildMode.Release, "Release")
}
