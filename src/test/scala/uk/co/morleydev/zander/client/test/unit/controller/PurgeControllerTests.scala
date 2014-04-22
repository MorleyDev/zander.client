package uk.co.morleydev.zander.client.test.unit.controller

import org.mockito.Mockito
import org.scalatest.FunSpec
import org.scalatest.mock.MockitoSugar
import uk.co.morleydev.zander.client.controller.PurgeController
import uk.co.morleydev.zander.client.service.PurgeProjectArtefacts
import uk.co.morleydev.zander.client.test.gen.GenModel

class PurgeControllerTests extends FunSpec with MockitoSugar {

  describe("Given a purge controller") {

    val mockPurge = mock[PurgeProjectArtefacts]
    val purgeController = new PurgeController(mockPurge)

    describe("When purging") {

      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()

      purgeController.apply(project, compiler, mode)

      it("Then the artefacts are purged") {
        Mockito.verify(mockPurge).apply(project, compiler, mode)
      }
    }
  }
}
