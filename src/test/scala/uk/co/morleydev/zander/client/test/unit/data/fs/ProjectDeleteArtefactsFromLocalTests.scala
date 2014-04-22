package uk.co.morleydev.zander.client.test.unit.data.fs

import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.data.fs.DeleteProjectArtefactsFromLocal
import uk.co.morleydev.zander.client.test.gen.GenModel
import java.io.File
import org.mockito.Mockito

class ProjectDeleteArtefactsFromLocalTests extends FunSpec with MockitoSugar {
  describe("Given project details containing artefacts") {

    val workingDirectory = new File("working")
    val mockDeleteFile = mock[File => Unit]
    val mockCleanDirectory = mock[File => Unit]

    val deleteFromlocal = new DeleteProjectArtefactsFromLocal(workingDirectory, mockDeleteFile, mockCleanDirectory)

    describe("When deleting files") {

      val artefactFiles = GenModel.store.genArtefactFiles()

      deleteFromlocal.apply(artefactFiles)

      it("Then the files are deleted") {
        artefactFiles
          .map(f => new File(workingDirectory, f))
          .foreach(f => Mockito.verify(mockDeleteFile).apply(f))
      }
      it("Then the root directories are cleanly deleted") {
        mockCleanDirectory(new File(workingDirectory, "include"))
        mockCleanDirectory(new File(workingDirectory, "lib"))
        mockCleanDirectory(new File(workingDirectory, "bin"))
      }
    }
  }
}
