package uk.co.morleydev.zander.client.unit.service.impl

import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.service.impl.CachedSourceAcquire
import java.io.File
import uk.co.morleydev.zander.client.data.{ProjectSourceUpdate, ProjectSourceDownload}
import uk.co.morleydev.zander.client.gen.GenModel
import org.mockito.{Matchers, Mockito}

class CachedSourceAcquireTests extends FunSpec with MockitoSugar {
  describe("Given a project and project dto") {

    val cache = new File("cache")
    val mockDirectoryIsExists = mock[(File => Boolean)]
    val mockSourceDownload = mock[ProjectSourceDownload]
    val mockSourceUpdate = mock[ProjectSourceUpdate]

    val gitCachedSourceAcquire = new CachedSourceAcquire(cache,
      mockDirectoryIsExists,
      mockSourceDownload,
      mockSourceUpdate)

    describe("When acquiring the source and the source does not already exist") {

      Mockito.when(mockDirectoryIsExists.apply(Matchers.any[File]))
        .thenReturn(false)

      val project = GenModel.arg.genProject()
      val dto = GenModel.net.genProjectDto()
      gitCachedSourceAcquire(project, dto)

      it("Then the source directories existence is checked") {
        Mockito.verify(mockDirectoryIsExists).apply(new File(cache, project.value + "/source"))
      }
      it("Then the source is downloaded") {
        Mockito.verify(mockSourceDownload).apply(project, dto)
      }
    }

    describe("When acquiring the source and the source does already exist") {

      Mockito.when(mockDirectoryIsExists.apply(Matchers.any[File]))
        .thenReturn(true)

      val project = GenModel.arg.genProject()
      val dto = GenModel.net.genProjectDto()
      gitCachedSourceAcquire(project, dto)

      it("Then the source directories existence is checked") {
        Mockito.verify(mockDirectoryIsExists).apply(new File(cache, project.value + "/source"))
      }
      it("Then the source is updated") {
        Mockito.verify(mockSourceUpdate).apply(project, dto)
      }
    }
  }
}
