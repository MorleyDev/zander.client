package uk.co.morleydev.zander.client.test.unit.service.impl

import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.data.GetProjectDto
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.{Branch, Project}
import uk.co.morleydev.zander.client.model.net.ProjectDto
import uk.co.morleydev.zander.client.service.impl.DownloadAcquireInstallProjectArtefactsFromCacheToLocal
import uk.co.morleydev.zander.client.service.{AcquireProjectArtefacts, CompileProjectSource, AcquireProjectSource}
import uk.co.morleydev.zander.client.test.gen.GenModel
import uk.co.morleydev.zander.client.test.unit.UnitTest
import scala.concurrent.future
import scala.concurrent.ExecutionContext.Implicits.global

class DownloadAcquireInstallProjectArtefactsFromCacheToLocalTests extends UnitTest {
  describe("Given project details and a dto to acquire the artefacts for") {
    val mockGetProjectDto = mock[GetProjectDto]
    val mockAcquireSource = mock[AcquireProjectSource]
    val mockCompileSource = mock[CompileProjectSource]
    val mockAcquireArtefacts = mock[AcquireProjectArtefacts]

    val acquirer = new DownloadAcquireInstallProjectArtefactsFromCacheToLocal(mockGetProjectDto,
      mockAcquireSource,
      mockCompileSource,
      mockAcquireArtefacts)

    describe("When acquiring and installing the details") {

      val sourceVersion = GenModel.store.genSourceVersion()
      Mockito.when(mockAcquireSource.apply(Matchers.any[Project], Matchers.any[ProjectDto], Matchers.any[Branch]))
             .thenReturn(sourceVersion)

      val dto = GenModel.net.genProjectDto()
      Mockito.when(mockGetProjectDto.apply(Matchers.any[Project], Matchers.any[BuildCompiler]))
        .thenReturn(future(dto))

      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()
      val branch = GenModel.arg.genBranch()

      acquirer.apply(project, compiler, mode, branch)

      it("Then the project dto is acquired") {
        Mockito.verify(mockGetProjectDto).apply(project, compiler)
      }
      it("Then the source is acquired") {
        Mockito.verify(mockAcquireSource).apply(project, dto, branch)
      }
      it("Then the source is compiled") {
        Mockito.verify(mockCompileSource).apply(project, compiler, mode, branch, sourceVersion)
      }
      it("Then the artefacts are acquired") {
        Mockito.verify(mockAcquireArtefacts).apply(project, compiler, mode, branch, sourceVersion)
      }
    }
  }
}
