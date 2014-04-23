package uk.co.morleydev.zander.client.test.unit.controller

import uk.co.morleydev.zander.client.test.unit.UnitTest
import uk.co.morleydev.zander.client.controller.UpdateController
import uk.co.morleydev.zander.client.test.gen.GenModel
import uk.co.morleydev.zander.client.service.exception.NoLocalArtefactsExistException

class UpdateControllerTests extends UnitTest {
  describe("Given an update controller") {

    val updateController = new UpdateController()

    describe("When updating and artefacts are not found") {

      val thrownException : Throwable = try {
        updateController.apply(GenModel.arg.genProject(), GenModel.arg.genCompiler(), GenModel.arg.genBuildMode())
        null
      } catch {
        case e : Throwable => e
      }

      it("Then an exception was thrown") {
        assert(thrownException != null)
      }

      it("Then the expected exception was thrown") {
        assert(thrownException.isInstanceOf[NoLocalArtefactsExistException])
      }
    }
  }
}
