package uk.co.morleydev.zander.client.test.unit.data.fs

import java.io.File
import org.mockito.Mockito
import uk.co.morleydev.zander.client.data.fs.InstallProjectArtefactFromCache
import uk.co.morleydev.zander.client.test.gen.GenModel
import uk.co.morleydev.zander.client.test.unit.UnitTest

class ProjectArtefactInstallFromCacheTests extends UnitTest {

  describe("Given a cache containing existing artefacts") {
    val cache = new File("some/cache/location")
    val workingDir = new File("")
    val mockCopyRecursive = mock[(File, File) => Unit]
    val projectArtefactInstall = new InstallProjectArtefactFromCache(cache, workingDir, mockCopyRecursive)

    describe("when installing artefacts from the cache") {
      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()

      projectArtefactInstall(project, compiler, mode)

      it("then the include file artefacts are copied recursively") {
        val expectedCacheProjectDirectory = new File(cache, project + "/" + compiler + "." + mode + "/include")
        Mockito.verify(mockCopyRecursive).apply(expectedCacheProjectDirectory, new File(workingDir, "include"))
      }
      it("then the lib file artefacts are copied recursively") {
        val expectedCacheProjectDirectory = new File(cache, project + "/" + compiler + "." + mode + "/lib")
        Mockito.verify(mockCopyRecursive).apply(expectedCacheProjectDirectory, new File(workingDir, "lib"))
      }
      it("then the bin file artefacts are copied recursively") {
        val expectedCacheProjectDirectory = new File(cache, project + "/" + compiler + "." + mode + "/bin")
        Mockito.verify(mockCopyRecursive).apply(expectedCacheProjectDirectory, new File(workingDir, "bin"))
      }
    }
  }

}
