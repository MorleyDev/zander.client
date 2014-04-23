package uk.co.morleydev.zander.client.test.unit.service.impl

import org.mockito.Mockito
import uk.co.morleydev.zander.client.service.impl.DownloadAcquireUpdateProjectArtefactsFromCacheToLocal
import uk.co.morleydev.zander.client.service.{DownloadAcquireInstallProjectArtefacts, PurgeProjectArtefacts}
import uk.co.morleydev.zander.client.test.gen.GenModel
import uk.co.morleydev.zander.client.test.unit.UnitTest

class DownloadAcquireUpdateProjectArtefactsFromCacheToLocalTests extends UnitTest {
  describe("Given a project/compiler/mode to update") {

    val mockPurgeArtefacts = mock[PurgeProjectArtefacts]
    val mockDownloadAcquireInstallProjectArtefacts = mock[DownloadAcquireInstallProjectArtefacts]
    val updateService = new DownloadAcquireUpdateProjectArtefactsFromCacheToLocal(mockPurgeArtefacts,
      mockDownloadAcquireInstallProjectArtefacts)

    describe("When updating") {

      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()

      updateService.apply(project, compiler, mode)

      it("Then the current artefacts are purged") {
        Mockito.verify(mockPurgeArtefacts).apply(project, compiler, mode)
      }
      it("Then the artefacts are acquired") {
        Mockito.verify(mockDownloadAcquireInstallProjectArtefacts).apply(project, compiler, mode)
      }
    }
  }

}
