package uk.co.morleydev.zander.client.test.unit.service.impl

import uk.co.morleydev.zander.client.data.{DeleteProjectArtefactDetails, DeleteProjectArtefacts, ReadProjectArtefactDetails}
import uk.co.morleydev.zander.client.controller.PurgeController
import uk.co.morleydev.zander.client.test.gen.GenModel
import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildCompiler._
import uk.co.morleydev.zander.client.model.arg.BuildMode._
import org.mockito.stubbing.Answer
import uk.co.morleydev.zander.client.model.store.ArtefactDetails
import org.mockito.invocation.InvocationOnMock
import java.io.FileNotFoundException
import uk.co.morleydev.zander.client.service.exception.NoLocalArtefactsExistException
import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.service.impl.LocalArtefactPurge

class LocalArtefactPurgeTests extends FunSpec with MockitoSugar {

  describe("Given a purge of local artefacts") {

    val mockProjectArtefactDetailsReader = mock[ReadProjectArtefactDetails]
    val mockProjectArtefactDeleteDetails = mock[DeleteProjectArtefactDetails]
    val mockProjectDeleteArtefacts = mock[DeleteProjectArtefacts]

    val localArtefactPurge = new LocalArtefactPurge(mockProjectArtefactDetailsReader, mockProjectArtefactDeleteDetails, mockProjectDeleteArtefacts)

    describe("When purging") {

      val artefactDetails = GenModel.store.genArtefactDetails()
      Mockito.when(mockProjectArtefactDetailsReader.apply(Matchers.any[Project], Matchers.any[BuildCompiler], Matchers.any[BuildMode]))
        .thenReturn(artefactDetails)

      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()

      localArtefactPurge.apply(project, compiler, mode)

      it("Then the artefact details are read") {
        Mockito.verify(mockProjectArtefactDetailsReader).apply(project, compiler, mode)
      }
      it("Then the artefacts are deleted") {
        Mockito.verify(mockProjectDeleteArtefacts).apply(artefactDetails)
      }
      it("Then the artefact details are deleted") {
        Mockito.verify(mockProjectArtefactDeleteDetails).apply(project, compiler, mode)
      }
    }
  }

  describe("Given a purge of artefacts") {

    val mockProjectArtefactDetailsReader = mock[ReadProjectArtefactDetails]
    val localArtefactPurge = new LocalArtefactPurge(mockProjectArtefactDetailsReader, null, null)

    describe("When purging and no artefact details are found") {

      Mockito.when(mockProjectArtefactDetailsReader.apply(Matchers.any[Project], Matchers.any[BuildCompiler], Matchers.any[BuildMode]))
        .thenAnswer(new Answer[ArtefactDetails] {
        override def answer(invocation: InvocationOnMock): ArtefactDetails = throw new FileNotFoundException
      })

      val thrownException : Throwable = try {

        localArtefactPurge.apply(GenModel.arg.genProject(), GenModel.arg.genCompiler(), GenModel.arg.genBuildMode())
        null
      } catch {
        case e : Throwable => e
      }

      it("Then an exception is thrown") {
        assert(thrownException != null)
      }
      it("Then the expected exception is thrown") {
        assert(thrownException.isInstanceOf[NoLocalArtefactsExistException],
          "Expected NoLocalArtefactsExistException but was %s".format(thrownException.getClass.getSimpleName))
      }
    }
  }
}
