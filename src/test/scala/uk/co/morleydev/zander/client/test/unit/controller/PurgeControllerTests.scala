package uk.co.morleydev.zander.client.test.unit.controller

import org.mockito.Mockito
import uk.co.morleydev.zander.client.controller.impl.PurgeController
import uk.co.morleydev.zander.client.service.PurgeProjectArtefacts
import uk.co.morleydev.zander.client.test.gen.GenModel
import uk.co.morleydev.zander.client.test.unit.UnitTest
import uk.co.morleydev.zander.client.validator.ValidateArtefactDetailsExistence

class PurgeControllerTests extends UnitTest {

  describe("Given a purge controller") {

    val mockValidate = mock[ValidateArtefactDetailsExistence]
    val mockPurge = mock[PurgeProjectArtefacts]
    val purgeController = new PurgeController(mockValidate, mockPurge)

    describe("When purging") {

      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()

      purgeController.apply(project, compiler, mode)

      it("Then the existence of artefact details is validated") {
        Mockito.verify(mockValidate).apply(project, compiler, mode)
      }
      it("Then the artefacts are purged") {
        Mockito.verify(mockPurge).apply(project, compiler, mode)
      }
    }
  }
}
