package uk.co.morleydev.zander.client.unit.data.fs

import org.scalatest.FunSpec
import org.scalatest.mock.MockitoSugar
import uk.co.morleydev.zander.client.data.fs.ProjectSourceDetailsReaderFromCache
import java.io.File
import uk.co.morleydev.zander.client.gen.{GenNative, GenModel}
import org.mockito.{Matchers, Mockito}
import com.lambdaworks.jacks.JacksMapper
import uk.co.morleydev.zander.client.model.store.SourceDetails

class ProjectSourceDetailsReaderFromCacheTests extends FunSpec with MockitoSugar {
  describe("Given a project source details reader") {

    val cache = new File("cache")
    val mockFileToStringReader = mock[File => String]
    val reader = new ProjectSourceDetailsReaderFromCache(cache, mockFileToStringReader)

    describe("When reading from the cache") {

      val expectedResultVersion = GenNative.genAsciiString(10, 100)
      Mockito.when(mockFileToStringReader.apply(Matchers.any[File]))
             .thenReturn(JacksMapper.writeValueAsString[SourceDetails](new SourceDetails(expectedResultVersion)))

      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()

      val result = reader(project, compiler, mode)

      it("Then the source details are read from the expected file") {
        Mockito.verify(mockFileToStringReader).apply(new File(cache, "/%s/%s.%s/version.json".format(project, compiler, mode)))
      }
      it("Then the expected Source Details are returned") {
        assert(result.version == expectedResultVersion)
      }
    }
  }
}
