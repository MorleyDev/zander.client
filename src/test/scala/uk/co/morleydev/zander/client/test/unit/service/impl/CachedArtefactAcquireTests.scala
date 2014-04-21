package uk.co.morleydev.zander.client.test.unit.service.impl

import org.mockito.Mockito
import org.scalatest.FunSpec
import org.scalatest.mock.MockitoSugar
import uk.co.morleydev.zander.client.service.impl.CachedArtefactAcquire
import uk.co.morleydev.zander.client.data.{ProjectSourceListFiles, ProjectArtefactVersionWriter, ProjectArtefactInstall}
import uk.co.morleydev.zander.client.test.gen.{GenNative, GenModel}

class CachedArtefactAcquireTests extends FunSpec with MockitoSugar {

  describe("Given a cached artefact acquire service") {
    val mockArtefactInstall = mock[ProjectArtefactInstall]
    val mockProjectSourceListFiles = mock[ProjectSourceListFiles]
    val mockArtefactVersionWriter = mock[ProjectArtefactVersionWriter]

    val cachedArtefactAcquire = new CachedArtefactAcquire(mockArtefactInstall,
      mockProjectSourceListFiles,
      mockArtefactVersionWriter)

    describe("When acquiring the cached artefacts") {
      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()
      val version = GenModel.store.genSourceVersion()

      val expectedFiles = GenNative.genSequence(1, 10, () => GenNative.genAlphaNumericString(1, 20))
      Mockito.when(mockProjectSourceListFiles.apply(project, compiler, mode))
             .thenReturn(expectedFiles)

      cachedArtefactAcquire.apply(project, compiler, mode, version)

      it("Then the artefacts are installed") {
        Mockito.verify(mockArtefactInstall).apply(project, compiler, mode)
      }
      it("Then the files are retrieved") {
        Mockito.verify(mockProjectSourceListFiles).apply(project, compiler, mode)
      }
      it("Then the version is written") {
        Mockito.verify(mockArtefactVersionWriter).apply(project, compiler, mode, version, expectedFiles)
      }
    }
  }
}
