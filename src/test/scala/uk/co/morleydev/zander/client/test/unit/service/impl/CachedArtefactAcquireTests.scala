package uk.co.morleydev.zander.client.test.unit.service.impl

import org.mockito.Mockito
import org.scalatest.FunSpec
import org.scalatest.mock.MockitoSugar
import uk.co.morleydev.zander.client.service.impl.AcquireCachedArtefacts
import uk.co.morleydev.zander.client.data.{ListProjectCacheFiles, WriteProjectArtefactVersion, InstallProjectArtefact}
import uk.co.morleydev.zander.client.test.gen.{GenNative, GenModel}

class CachedArtefactAcquireTests extends FunSpec with MockitoSugar {

  describe("Given a cached artefact acquire service") {
    val mockArtefactInstall = mock[InstallProjectArtefact]
    val mockProjectSourceListFiles = mock[ListProjectCacheFiles]
    val mockArtefactVersionWriter = mock[WriteProjectArtefactVersion]

    val cachedArtefactAcquire = new AcquireCachedArtefacts(mockArtefactInstall,
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
