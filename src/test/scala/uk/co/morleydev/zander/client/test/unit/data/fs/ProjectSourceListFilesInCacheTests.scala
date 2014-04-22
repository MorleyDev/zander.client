package uk.co.morleydev.zander.client.test.unit.data.fs

import org.scalatest.FunSpec
import org.scalatest.mock.MockitoSugar
import uk.co.morleydev.zander.client.data.fs.ListProjectCacheFilesInCache
import java.io.File
import uk.co.morleydev.zander.client.test.gen.GenModel
import org.mockito.Mockito

class ProjectSourceListFilesInCacheTests extends FunSpec with MockitoSugar {

  describe("Given a cache") {
    val mockListFilesInDirectory = mock[File => Seq[File]]

    val cache = new File("cache")
    val projectSourceListFilesInCache = new ListProjectCacheFilesInCache(cache, mockListFilesInDirectory)

    describe("When listing the files in the cache for a project/compiler/mode") {
      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()

      val artefactDirectory = new File(cache, "%s/%s.%s".format(project, compiler, mode))
      Mockito.when(mockListFilesInDirectory.apply(new File(artefactDirectory, "include")))
        .thenReturn(Seq[File](new File(artefactDirectory, "include/some/file"),
        new File(artefactDirectory, "include/include_file")))
      Mockito.when(mockListFilesInDirectory.apply(new File(artefactDirectory, "lib")))
             .thenReturn(Seq[File](new File(artefactDirectory, "lib/some/file"),
                                   new File(artefactDirectory, "lib/include_file")))
      Mockito.when(mockListFilesInDirectory.apply(new File(artefactDirectory, "bin")))
        .thenReturn(Seq[File](new File(artefactDirectory, "bin/some/file"),
        new File(artefactDirectory, "bin/include_file")))

      val result = projectSourceListFilesInCache(project, compiler, mode)

      it("Then the expected result is returned") {
        val expectedFiles = Seq[String](
          new File("include/some/file").getPath,
          new File("include/include_file").getPath,
          new File("lib/some/file").getPath,
          new File("lib/include_file").getPath,
          new File("bin/some/file").getPath,
          new File("bin/include_file").getPath)
        assert(result.diff(expectedFiles).size == 0)
      }
    }
  }

}
