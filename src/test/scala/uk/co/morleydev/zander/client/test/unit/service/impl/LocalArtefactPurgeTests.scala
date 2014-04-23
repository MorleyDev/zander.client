package uk.co.morleydev.zander.client.test.unit.service.impl

import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.data.{ProcessProjectArtefactDetailsMap, DeleteProjectArtefactDetails, DeleteProjectArtefacts}
import uk.co.morleydev.zander.client.model.arg.BuildCompiler._
import uk.co.morleydev.zander.client.model.arg.BuildMode._
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.store.ArtefactDetails
import uk.co.morleydev.zander.client.service.GetAllProjectArtefactDetails
import uk.co.morleydev.zander.client.service.exception.NoLocalArtefactsExistException
import uk.co.morleydev.zander.client.service.impl.LocalArtefactPurge
import uk.co.morleydev.zander.client.test.gen.GenModel
import uk.co.morleydev.zander.client.test.unit.UnitTest

class LocalArtefactPurgeTests extends UnitTest {

  describe("Given a purge of local artefacts") {

    val mockGetAllProjectArtefactDetails = mock[GetAllProjectArtefactDetails]
    val mockProcessProjectArtefactDetails = mock[ProcessProjectArtefactDetailsMap]
    val mockProjectArtefactDeleteDetails = mock[DeleteProjectArtefactDetails]
    val mockProjectDeleteArtefacts = mock[DeleteProjectArtefacts]

    val localArtefactPurge = new LocalArtefactPurge(mockGetAllProjectArtefactDetails,
      mockProcessProjectArtefactDetails,
      mockProjectArtefactDeleteDetails,
      mockProjectDeleteArtefacts)

    describe("When purging") {

      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()

      val artefactDetails = GenModel.store.genArtefactDetails()
      val projectArtefactDetailsKeyValue = (project, compiler, mode) -> artefactDetails
      val projectArtefactDetailsMap = Map[(Project, BuildCompiler, BuildMode), ArtefactDetails](projectArtefactDetailsKeyValue)

      Mockito.when(mockGetAllProjectArtefactDetails.apply())
        .thenReturn(projectArtefactDetailsMap)
      Mockito.when(mockProcessProjectArtefactDetails.apply(Matchers.any[Map[(Project, BuildCompiler, BuildMode), ArtefactDetails]]))
        .thenReturn(projectArtefactDetailsMap)

      localArtefactPurge.apply(project, compiler, mode)

      it("Then the artefact details are read") {
        Mockito.verify(mockGetAllProjectArtefactDetails).apply()
      }
      it("Then the details are processed") {
        Mockito.verify(mockProcessProjectArtefactDetails).apply(projectArtefactDetailsMap)
      }
      it("Then the expected artefacts are deleted") {
        Mockito.verify(mockProjectDeleteArtefacts).apply(artefactDetails.files)
      }
      it("Then the expected artefact details are deleted") {
        Mockito.verify(mockProjectArtefactDeleteDetails).apply(project, compiler, mode)
      }
    }
  }

  describe("Given a purge of artefacts") {

    val mockGetAllProjectArtefactDetails = mock[GetAllProjectArtefactDetails]
    val mockProcessProjectArtefactDetails = mock[ProcessProjectArtefactDetailsMap]
    val localArtefactPurge = new LocalArtefactPurge(mockGetAllProjectArtefactDetails, mockProcessProjectArtefactDetails, null, null)

    describe("When purging and no artefact details are found") {

      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()

      val artefactDetails = GenModel.store.genArtefactDetails()
      val projectArtefactDetailsKeyValue = (project, compiler, mode) -> artefactDetails

      val details = Map[(Project, BuildCompiler, BuildMode), ArtefactDetails](projectArtefactDetailsKeyValue)
      Mockito.when(mockGetAllProjectArtefactDetails.apply())
        .thenReturn(details)

      Mockito.when(mockProcessProjectArtefactDetails.apply(Matchers.any[Map[(Project, BuildCompiler, BuildMode), ArtefactDetails]]))
        .thenReturn(Map[(Project, BuildCompiler, BuildMode), ArtefactDetails]())

      val thrownException : Throwable = try {

        localArtefactPurge.apply(project, compiler, mode)
        null
      } catch {
        case e : Throwable => e
      }

      it("Then the details are processed") {
        Mockito.verify(mockProcessProjectArtefactDetails).apply(details)
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
