package uk.co.morleydev.zander.client.test.unit.service.impl

import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.service.impl.CachedSourceAcquire
import java.io.File
import uk.co.morleydev.zander.client.data.{GetProjectSourceVersion, ProjectSourceUpdate, ProjectSourceDownload}
import uk.co.morleydev.zander.client.test.gen.GenModel
import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.model.arg.Project

class CachedSourceAcquireTests extends FunSpec with MockitoSugar {
  describe("Given a project and project dto") {

    val cache = new File("cache")
    val mockDirectoryIsExists = mock[(File => Boolean)]
    val mockSourceDownload = mock[ProjectSourceDownload]
    val mockSourceUpdate = mock[ProjectSourceUpdate]
    val mockGetSourceVersion = mock[GetProjectSourceVersion]

    val gitCachedSourceAcquire = new CachedSourceAcquire(cache,
      mockDirectoryIsExists,
      mockSourceDownload,
      mockSourceUpdate,
      mockGetSourceVersion)

    describe("When acquiring the source and the source does not already exist") {

      Mockito.when(mockDirectoryIsExists.apply(Matchers.any[File]))
        .thenReturn(false)

      val expectedSourceVersion = GenModel.store.genSourceVersion()
      Mockito.when(mockGetSourceVersion.apply(Matchers.any[Project]))
        .thenReturn(expectedSourceVersion)

      val project = GenModel.arg.genProject()
      val dto = GenModel.net.genProjectDto()
      val actualSourceVersion = gitCachedSourceAcquire(project, dto)

      it("Then the source directories existence is checked") {
        Mockito.verify(mockDirectoryIsExists).apply(new File(cache, project.value + "/source"))
      }
      it("Then the source is downloaded") {
        Mockito.verify(mockSourceDownload).apply(project, dto)
      }
      it("Then the source version is retrieved") {
        Mockito.verify(mockGetSourceVersion).apply(project)
      }
      it("Then the source version is returned") {
        assert(actualSourceVersion == expectedSourceVersion)
      }
    }

    describe("When acquiring the source and the source does already exist") {

      Mockito.when(mockDirectoryIsExists.apply(Matchers.any[File]))
        .thenReturn(true)

      val expectedSourceVersion = GenModel.store.genSourceVersion()
      Mockito.when(mockGetSourceVersion.apply(Matchers.any[Project]))
        .thenReturn(expectedSourceVersion)

      val project = GenModel.arg.genProject()
      val dto = GenModel.net.genProjectDto()
      val actualSourceVersion = gitCachedSourceAcquire(project, dto)

      it("Then the source directories existence is checked") {
        Mockito.verify(mockDirectoryIsExists).apply(new File(cache, project.value + "/source"))
      }
      it("Then the source is updated") {
        Mockito.verify(mockSourceUpdate).apply(project, dto)
      }
      it("Then the source version is retrieved") {
        Mockito.verify(mockGetSourceVersion).apply(project)
      }
      it("Then the source version is returned") {
        assert(actualSourceVersion == expectedSourceVersion)
      }
    }
  }
}
