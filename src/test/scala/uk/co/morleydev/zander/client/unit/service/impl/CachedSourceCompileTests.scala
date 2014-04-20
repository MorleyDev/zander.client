package uk.co.morleydev.zander.client.unit.service.impl

import org.scalatest.FunSpec
import org.scalatest.mock.MockitoSugar
import uk.co.morleydev.zander.client.service.impl.CachedSourceCompile
import uk.co.morleydev.zander.client.data._
import uk.co.morleydev.zander.client.gen.GenModel
import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import java.io.FileNotFoundException
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock
import uk.co.morleydev.zander.client.model.store.SourceDetails

class CachedSourceCompileTests extends FunSpec with MockitoSugar {
  describe("Given a source details reader for the project does not exist") {

    val mockSourceDetailsReader = mock[ProjectSourceDetailsReader]
    Mockito.when(mockSourceDetailsReader.apply(Matchers.any[Project], Matchers.any[BuildCompiler], Matchers.any[BuildMode]))
           .thenAnswer(new Answer[SourceDetails] {
              override def answer(invocation: InvocationOnMock): SourceDetails = {
                throw new FileNotFoundException()
              }
           })

    val mockPrebuild = mock[ProjectSourcePrebuild]
    val mockBuild = mock[ProjectSourceBuild]
    val mockInstall = mock[ProjectSourceInstall]
    val mockSourceDetailsWriter = mock[ProjectSourceDetailsWriter]

    val cachedSourceCompile = new CachedSourceCompile(mockSourceDetailsReader,
                                                      mockPrebuild,
                                                      mockBuild,
                                                      mockInstall,
                                                      mockSourceDetailsWriter)

    describe("When compiling artefacts") {
      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()
      val version = GenModel.store.genSourceVersion()

      cachedSourceCompile.apply(project, compiler, mode, version)

      it("Then the source version is read") {
        Mockito.verify(mockSourceDetailsReader).apply(project, compiler, mode)
      }
      it("Then the source is prebuilt") {
        Mockito.verify(mockPrebuild).apply(project, compiler, mode)
      }
      it("Then the source is built") {
        Mockito.verify(mockBuild).apply(project, compiler, mode)
      }
      it("Then the source is installed to the cache") {
        Mockito.verify(mockInstall).apply(project, compiler, mode)
      }
      it("Then the source details are written to the cache") {
        Mockito.verify(mockSourceDetailsWriter).apply(project, compiler, mode, version)
      }
    }
  }

  describe("Given a source details reader for the project does exist") {

    val mockSourceDetailsReader = mock[ProjectSourceDetailsReader]

    val mockPrebuild = mock[ProjectSourcePrebuild]
    val mockBuild = mock[ProjectSourceBuild]
    val mockInstall = mock[ProjectSourceInstall]
    val mockSourceDetailsWriter = mock[ProjectSourceDetailsWriter]

    val cachedSourceCompile = new CachedSourceCompile(mockSourceDetailsReader,
      mockPrebuild,
      mockBuild,
      mockInstall,
      mockSourceDetailsWriter)

    describe("When compiling artefacts and the version is current") {
      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()
      val version = GenModel.store.genSourceVersion()
      Mockito.when(mockSourceDetailsReader.apply(Matchers.any[Project], Matchers.any[BuildCompiler], Matchers.any[BuildMode]))
        .thenReturn(new SourceDetails(version.value))

      cachedSourceCompile.apply(project, compiler, mode, version)

      it("Then the source version is read") {
        Mockito.verify(mockSourceDetailsReader).apply(project, compiler, mode)
      }
      it("Then the source is not prebuilt") {
        Mockito.verify(mockPrebuild, Mockito.never()).apply(project, compiler, mode)
      }
      it("Then the source is not built") {
        Mockito.verify(mockBuild, Mockito.never()).apply(project, compiler, mode)
      }
      it("Then the source is not installed to the cache") {
        Mockito.verify(mockInstall, Mockito.never()).apply(project, compiler, mode)
      }
      it("Then the source details are not written to the cache") {
        Mockito.verify(mockSourceDetailsWriter, Mockito.never()).apply(project, compiler, mode, version)
      }
    }
  }
}
