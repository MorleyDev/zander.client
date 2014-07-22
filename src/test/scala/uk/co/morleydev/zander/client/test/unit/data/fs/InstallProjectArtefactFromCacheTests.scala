package uk.co.morleydev.zander.client.test.unit.data.fs

import java.io.File
import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.data.GetArtefactsLocation
import uk.co.morleydev.zander.client.data.fs.InstallProjectArtefactFromCache
import uk.co.morleydev.zander.client.model.arg.{Project, Branch}
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.test.gen.GenModel
import uk.co.morleydev.zander.client.test.unit.UnitTest

class InstallProjectArtefactFromCacheTests extends UnitTest {

  describe("Given a cache containing existing artefacts") {
    val cache = new File("some/cache/location")
    val mockGetArtefactsLocation = mock[GetArtefactsLocation]
    Mockito.when(mockGetArtefactsLocation.apply(Matchers.any[Project], Matchers.any[BuildCompiler], Matchers.any[BuildMode], Matchers.any[Branch]))
           .thenReturn(cache)

    val workingDir = new File("")
    val mockCopyRecursive = mock[(File, File) => Unit]
    val projectArtefactInstall = new InstallProjectArtefactFromCache(mockGetArtefactsLocation, workingDir, mockCopyRecursive)

    describe("when installing artefacts from the cache") {
      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()
      val branch = GenModel.arg.genBranch()

      projectArtefactInstall(project, compiler, mode, branch)

      it("then the artefacts location is retrieved") {
        Mockito.verify(mockGetArtefactsLocation).apply(project, compiler, mode, branch)
      }
      it("then the include file artefacts are copied recursively") {
        val expectedCacheProjectDirectory = new File(cache, "include")
        Mockito.verify(mockCopyRecursive).apply(expectedCacheProjectDirectory, new File(workingDir, "include"))
      }
      it("then the lib file artefacts are copied recursively") {
        val expectedCacheProjectDirectory = new File(cache, "lib")
        Mockito.verify(mockCopyRecursive).apply(expectedCacheProjectDirectory, new File(workingDir, "lib"))
      }
      it("then the bin file artefacts are copied recursively") {
        val expectedCacheProjectDirectory = new File(cache, "bin")
        Mockito.verify(mockCopyRecursive).apply(expectedCacheProjectDirectory, new File(workingDir, "bin"))
      }
    }
  }

}
