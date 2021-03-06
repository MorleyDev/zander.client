package uk.co.morleydev.zander.client.test.unit.data.fs

import java.io.File
import org.mockito.Mockito
import uk.co.morleydev.zander.client.data.fs.DeleteProjectArtefactDetailsFromLocal
import uk.co.morleydev.zander.client.test.gen.GenModel
import uk.co.morleydev.zander.client.test.unit.UnitTest

class ProjectArtefactDeleteDetailsFromLocalTests extends UnitTest {
  describe("Given a project/compiler/mode to delete") {

    val workingDirectory = new File("working")
    val mockFileDelete = mock[File => Unit]
    val deleteDetails = new DeleteProjectArtefactDetailsFromLocal(workingDirectory, mockFileDelete)

    describe("When deleting") {
      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()

      deleteDetails.apply(project, compiler, mode)

      it("Then the expected file is deleted") {
        Mockito.verify(mockFileDelete).apply(new File(workingDirectory, "%s.%s.%s.json".format(project, compiler, mode)))
      }
    }
  }
}
