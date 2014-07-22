package uk.co.morleydev.zander.client.test.unit.data.fs

import com.lambdaworks.jacks.JacksMapper
import java.io.File
import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.data._
import uk.co.morleydev.zander.client.data.fs.WriteProjectSourceDetailsToCache
import uk.co.morleydev.zander.client.model.arg.{Project, Branch}
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.store.CacheDetails
import uk.co.morleydev.zander.client.test.gen.GenModel
import uk.co.morleydev.zander.client.test.unit.UnitTest

class ProjectSourceVersionWriterToCacheTests extends UnitTest {
  describe("Given a project source version writer") {

    val cache = new File("cache/of/some/description")
    val mockGetArtefactsLocation = mock[GetArtefactsLocation]
    Mockito.when(mockGetArtefactsLocation.apply(Matchers.any[Project], Matchers.any[BuildCompiler], Matchers.any[BuildMode], Matchers.any[Branch]))
           .thenReturn(cache)

    val mockWriteStringToFile = mock[(String, File) => Unit]
    val writer = new WriteProjectSourceDetailsToCache(mockGetArtefactsLocation,  mockWriteStringToFile)

    describe("when writing a source version to the cache") {

      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()
      val branch = GenModel.arg.genBranch()
      val version = GenModel.store.genSourceVersion()

      writer.apply(project, compiler, mode, branch, version)

      it("Then the source details are written") {

        val expectedSourceDetailsJson = JacksMapper.writeValueAsString[CacheDetails](new CacheDetails(version.value))

        Mockito.verify(mockWriteStringToFile)(expectedSourceDetailsJson, new File(cache, "version.json"))
      }
    }
  }
}
