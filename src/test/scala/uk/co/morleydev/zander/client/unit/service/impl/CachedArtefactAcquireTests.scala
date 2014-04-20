package uk.co.morleydev.zander.client.unit.service.impl

import org.mockito.Mockito
import org.scalatest.FunSpec
import org.scalatest.mock.MockitoSugar
import uk.co.morleydev.zander.client.service.impl.CachedArtefactAcquire
import uk.co.morleydev.zander.client.data.{ProjectArtefactVersionWriter, ProjectArtefactInstall}
import uk.co.morleydev.zander.client.gen.GenModel

class CachedArtefactAcquireTests extends FunSpec with MockitoSugar {

  describe("Given a cached artefact acquire service") {
    val mockArtefactInstall = mock[ProjectArtefactInstall]
    val mockArtefactVersionWriter = mock[ProjectArtefactVersionWriter]

    val cachedArtefactAcquire = new CachedArtefactAcquire(mockArtefactInstall, mockArtefactVersionWriter)

    describe("When acquiring the cached artefacts") {
      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()
      val version = GenModel.store.genSourceVersion()

      cachedArtefactAcquire.apply(project, compiler, mode, version)

      it("Then the artefacts are installed") {
        Mockito.verify(mockArtefactInstall).apply(project, compiler, mode)
      }
      it("Then the version is written") {
        Mockito.verify(mockArtefactVersionWriter).apply(project, compiler, mode, version)
      }
    }
  }
}
