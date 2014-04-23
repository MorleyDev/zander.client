package uk.co.morleydev.zander.client.test.unit.controller

import org.mockito.Mockito
import uk.co.morleydev.zander.client.controller.UpdateController
import uk.co.morleydev.zander.client.test.gen.GenModel
import uk.co.morleydev.zander.client.test.unit.UnitTest
import uk.co.morleydev.zander.client.validator.ValidateArtefactDetailsExistence
import uk.co.morleydev.zander.client.service.{PurgeProjectArtefacts, DownloadAcquireInstallProjectArtefacts}

class UpdateControllerTests extends UnitTest {
  describe("Given an update controller") {

    val mockValidateArtefactDetailsExist = mock[ValidateArtefactDetailsExistence]
    val mockPurgeArtefacts = mock[PurgeProjectArtefacts]
    val mockDownloadAcquireInstallProjectArtefacts = mock[DownloadAcquireInstallProjectArtefacts]
    val updateController = new UpdateController(mockValidateArtefactDetailsExist,
                                                mockPurgeArtefacts,
                                                mockDownloadAcquireInstallProjectArtefacts)

    describe("When updating") {

      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()

      updateController.apply(project, compiler, mode)

      it("Then the artefact detail existence was validated") {
        Mockito.verify(mockValidateArtefactDetailsExist).apply(project, compiler, mode)
      }
      it("Then the current artefacts are purged") {
        Mockito.verify(mockPurgeArtefacts).apply(project, compiler, mode)
      }
      it("Then the artefacts are acquired") {
        Mockito.verify(mockDownloadAcquireInstallProjectArtefacts).apply(project, compiler, mode)
      }
    }
  }
}
