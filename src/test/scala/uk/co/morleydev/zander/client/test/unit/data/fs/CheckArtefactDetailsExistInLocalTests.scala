package uk.co.morleydev.zander.client.test.unit.data.fs

import uk.co.morleydev.zander.client.test.unit.UnitTest
import java.io.{FileNotFoundException, File}
import uk.co.morleydev.zander.client.data.fs.CheckArtefactDetailsExistInLocal
import uk.co.morleydev.zander.client.data.ReadProjectArtefactDetails
import org.mockito.{Mockito, Matchers}
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.test.gen.GenModel
import org.mockito.stubbing.Answer
import uk.co.morleydev.zander.client.model.store.ArtefactDetails
import org.mockito.invocation.InvocationOnMock

class CheckArtefactDetailsExistInLocalTests extends UnitTest {
  describe("Given a working directory When checking if artefact details exist in the local succeeds") {
    val mockReadProjectArtefactDetails = mock[ReadProjectArtefactDetails]

    val check = new CheckArtefactDetailsExistInLocal(new File("working"), mockReadProjectArtefactDetails)

    Mockito.when(mockReadProjectArtefactDetails.apply(Matchers.any[Project], Matchers.any[BuildCompiler], Matchers.any[BuildMode]))
          .thenReturn(GenModel.store.genArtefactDetails())

    val project = GenModel.arg.genProject()
    val compiler = GenModel.arg.genCompiler()
    val mode = GenModel.arg.genBuildMode()

    val result = check.apply(project, compiler, mode)

    it("Then the artefact details are read") {
      Mockito.verify(mockReadProjectArtefactDetails).apply(project, compiler, mode)
    }
    it("Then true is returned") {
      assert(result)
    }
  }

  describe("Given a working directory When checking if artefact details exist in the local fails") {
    val mockReadProjectArtefactDetails = mock[ReadProjectArtefactDetails]

    val check = new CheckArtefactDetailsExistInLocal(new File("working"), mockReadProjectArtefactDetails)

    Mockito.when(mockReadProjectArtefactDetails.apply(Matchers.any[Project], Matchers.any[BuildCompiler], Matchers.any[BuildMode]))
      .thenAnswer(new Answer[ArtefactDetails] {
      override def answer(invocation: InvocationOnMock): ArtefactDetails = throw new FileNotFoundException
    })

    val project = GenModel.arg.genProject()
    val compiler = GenModel.arg.genCompiler()
    val mode = GenModel.arg.genBuildMode()

    val result = check.apply(project, compiler, mode)

    it("Then false is returned") {
      assert(!result)
    }
  }
}
