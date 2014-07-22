package uk.co.morleydev.zander.client.test.unit.controller

import org.mockito.Mockito
import uk.co.morleydev.zander.client.model.OperationArguments
import uk.co.morleydev.zander.client.service.DownloadAcquireInstallProjectArtefacts
import uk.co.morleydev.zander.client.test.gen.GenModel
import uk.co.morleydev.zander.client.test.unit.UnitTest
import uk.co.morleydev.zander.client.validator.ValidateArtefactDetailsExistence
import uk.co.morleydev.zander.client.controller.impl.InstallController

class InstallControllerTests extends UnitTest {
  describe("Given an install controller") {
    val mockValidator = mock[ValidateArtefactDetailsExistence]
    val mockAcquireAndInstall = mock[DownloadAcquireInstallProjectArtefacts]

    val installController = new InstallController(mockValidator,
                                                  mockAcquireAndInstall)

    describe("when installing a project and no local artefacts exist") {


      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()
      val branch = GenModel.arg.genBranch()

      installController(new OperationArguments(project, compiler, mode, branch))

      it("Then the artefact details are validated") {
        Mockito.verify(mockValidator).apply(project, compiler, mode)
      }
      it("Then the artefacts are downloaded, compiled and acquired") {
        Mockito.verify(mockAcquireAndInstall).apply(project, compiler, mode, branch)
      }
    }
  }
}
