package uk.co.morleydev.zander.client.test.unit.controller

import org.mockito.Mockito
import uk.co.morleydev.zander.client.controller.impl.UpdateController
import uk.co.morleydev.zander.client.model.OperationArguments
import uk.co.morleydev.zander.client.service.DownloadAcquireUpdateProjectArtefacts
import uk.co.morleydev.zander.client.test.gen.GenModel
import uk.co.morleydev.zander.client.test.unit.UnitTest
import uk.co.morleydev.zander.client.validator.ValidateArtefactDetailsExistence

class UpdateControllerTests extends UnitTest {
  describe("Given an update controller") {

    val mockValidateArtefactDetailsExist = mock[ValidateArtefactDetailsExistence]
    val mockDownloadAcquireUpdateProjectArtefacts = mock[DownloadAcquireUpdateProjectArtefacts]
    val updateController = new UpdateController(mockValidateArtefactDetailsExist,
                                                mockDownloadAcquireUpdateProjectArtefacts)

    describe("When updating") {

      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()
      val branch = GenModel.arg.genBranch()

      updateController.apply(new OperationArguments(project, compiler, mode, branch))

      it("Then the artefact detail existence was validated") {
        Mockito.verify(mockValidateArtefactDetailsExist).apply(project, compiler, mode)
      }
      it("Then the artefacts are updated") {
        Mockito.verify(mockDownloadAcquireUpdateProjectArtefacts).apply(project, compiler, mode, branch)
      }
    }
  }
}
