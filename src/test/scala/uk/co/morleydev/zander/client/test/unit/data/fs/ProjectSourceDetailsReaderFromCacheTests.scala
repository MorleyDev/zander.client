package uk.co.morleydev.zander.client.test.unit.data.fs

import com.lambdaworks.jacks.JacksMapper
import java.io.File
import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.data.fs.ReadProjectCacheDetailsFromCache
import uk.co.morleydev.zander.client.model.store.CacheDetails
import uk.co.morleydev.zander.client.test.gen.{GenNative, GenModel}
import uk.co.morleydev.zander.client.test.unit.UnitTest

class ProjectSourceDetailsReaderFromCacheTests extends UnitTest {
  describe("Given a project source details reader") {

    val cache = new File("cache")
    val mockFileToStringReader = mock[File => String]
    val reader = new ReadProjectCacheDetailsFromCache(cache, mockFileToStringReader)

    describe("When reading from the cache") {

      val expectedResultVersion = GenNative.genAsciiString(10, 100)
      Mockito.when(mockFileToStringReader.apply(Matchers.any[File]))
             .thenReturn(JacksMapper.writeValueAsString[CacheDetails](new CacheDetails(expectedResultVersion)))

      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()

      val result = reader(project, compiler, mode)

      it("Then the source details are read from the expected file") {
        Mockito.verify(mockFileToStringReader).apply(new File(cache, "%s/%s.%s/version.json".format(project, compiler, mode)))
      }
      it("Then the expected Source Details are returned") {
        assert(result.version == expectedResultVersion)
      }
    }
  }
}
