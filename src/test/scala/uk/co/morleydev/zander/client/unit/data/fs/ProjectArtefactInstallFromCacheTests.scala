package uk.co.morleydev.zander.client.unit.data.fs

import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSpec
import java.io.File
import uk.co.morleydev.zander.client.data.fs.ProjectArtefactInstallFromCache
import uk.co.morleydev.zander.client.gen.GenModel
import org.mockito.Mockito

class ProjectArtefactInstallFromCacheTests extends FunSpec with MockitoSugar {

  describe("Given a cache containing existing artefacts") {
    val cache = new File("some/cache/location")
    val workingDir = new File("")
    val mockCopyRecursive = mock[(File, File) => Unit]
    val projectArtefactInstall = new ProjectArtefactInstallFromCache(cache, workingDir, mockCopyRecursive)

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
