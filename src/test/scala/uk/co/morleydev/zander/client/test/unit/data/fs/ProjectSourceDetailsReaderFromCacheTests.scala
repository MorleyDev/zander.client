package uk.co.morleydev.zander.client.test.unit.data.fs

import com.lambdaworks.jacks.JacksMapper
import java.io.File
import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.data.GetArtefactsLocation
import uk.co.morleydev.zander.client.data.fs.ReadProjectCacheDetailsFromCache
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.{Branch, Project}
import uk.co.morleydev.zander.client.model.store.CacheDetails
import uk.co.morleydev.zander.client.test.gen.{GenNative, GenModel}
import uk.co.morleydev.zander.client.test.unit.UnitTest

class ProjectSourceDetailsReaderFromCacheTests extends UnitTest {
  describe("Given a project source details reader") {

    val cache = new File("cache")
    val mockGetArtefactsLocation = mock[GetArtefactsLocation]
    Mockito.when(mockGetArtefactsLocation.apply(Matchers.any[Project], Matchers.any[BuildCompiler], Matchers.any[BuildMode], Matchers.any[Branch]))
           .thenReturn(cache)

    val mockFileToStringReader = mock[File => String]
    val reader = new ReadProjectCacheDetailsFromCache(mockGetArtefactsLocation, mockFileToStringReader)

    describe("When reading from the cache") {

      val expectedResultVersion = GenNative.genAsciiString(10, 100)
      Mockito.when(mockFileToStringReader.apply(Matchers.any[File]))
             .thenReturn(JacksMapper.writeValueAsString[CacheDetails](new CacheDetails(expectedResultVersion)))

      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()
      val branch = GenModel.arg.genBranch()

      val result = reader(project, compiler, mode, branch)

      it("Then the cache location is retrieved") {
        Mockito.verify(mockGetArtefactsLocation).apply(project, compiler, mode, branch)
      }
      it("Then the source details are read from the expected file") {
        Mockito.verify(mockFileToStringReader).apply(new File(cache, "version.json"))
      }
      it("Then the expected Source Details are returned") {
        assert(result.version == expectedResultVersion)
      }
    }
  }
}
