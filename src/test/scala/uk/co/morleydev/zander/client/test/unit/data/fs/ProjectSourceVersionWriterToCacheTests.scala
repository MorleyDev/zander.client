package uk.co.morleydev.zander.client.test.unit.data.fs

import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.data.fs.WriteProjectSourceDetailsToCache
import uk.co.morleydev.zander.client.test.gen.GenModel
import com.lambdaworks.jacks.JacksMapper
import uk.co.morleydev.zander.client.model.store.CacheDetails
import java.io.File
import org.mockito.Mockito

class ProjectSourceVersionWriterToCacheTests extends FunSpec with MockitoSugar {
  describe("Given a project source version writer") {

    val cache = new File("cache")
    val mockWriteStringToFile = mock[(String, File) => Unit]
    val writer = new WriteProjectSourceDetailsToCache(cache,  mockWriteStringToFile)

    describe("when writing a source version to the cache") {

      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()
      val version = GenModel.store.genSourceVersion()

      writer.apply(project, compiler, mode, version)

      it("Then the source details are written") {

        val expectedSourceDetailsJson = JacksMapper.writeValueAsString[CacheDetails](new CacheDetails(version.value))

        Mockito.verify(mockWriteStringToFile)(expectedSourceDetailsJson,
                                              new File(cache, "/%s/%s.%s/version.json".format(project, compiler, mode)))
      }
    }
  }
}
