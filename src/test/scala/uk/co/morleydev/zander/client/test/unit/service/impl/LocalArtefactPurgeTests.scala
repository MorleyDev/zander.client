package uk.co.morleydev.zander.client.test.unit.service.impl

import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.data.{ProcessProjectArtefactDetailsMap, DeleteProjectArtefactDetails, DeleteProjectArtefacts}
import uk.co.morleydev.zander.client.model.arg.BuildCompiler._
import uk.co.morleydev.zander.client.model.arg.BuildMode._
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.store.ArtefactDetails
import uk.co.morleydev.zander.client.service.GetAllProjectArtefactDetails
import uk.co.morleydev.zander.client.service.impl.LocalArtefactPurge
import uk.co.morleydev.zander.client.test.gen.GenModel
import uk.co.morleydev.zander.client.test.unit.UnitTest
import uk.co.morleydev.zander.client.validator.exception.NoLocalArtefactsExistException

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
}
