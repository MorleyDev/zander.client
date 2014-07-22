package uk.co.morleydev.zander.client.test.unit.service.impl

import scala.concurrent.future
import scala.concurrent.ExecutionContext.Implicits.global
import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.service.{AcquireProjectArtefacts, CompileProjectSource, AcquireProjectSource, PurgeProjectArtefacts}
import uk.co.morleydev.zander.client.service.impl.DownloadAcquireUpdateProjectArtefactsFromCacheToLocal
import uk.co.morleydev.zander.client.test.gen.GenModel
import uk.co.morleydev.zander.client.test.unit.UnitTest
import uk.co.morleydev.zander.client.data.{ReadProjectArtefactDetails, GetProjectDto}
import uk.co.morleydev.zander.client.model.net.ProjectDto
import uk.co.morleydev.zander.client.model.arg.{Branch, Project}
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.store.ArtefactDetails

class DownloadAcquireUpdateProjectArtefactsFromCacheToLocalTests extends UnitTest {
  describe("Given project details and a dto to acquire the artefacts for") {
    val mockGetProjectDto = mock[GetProjectDto]
    val mockAcquireSource = mock[AcquireProjectSource]
    val mockCompileSource = mock[CompileProjectSource]
    val mockReadArtefactDetails = mock[ReadProjectArtefactDetails]
    val mockPurgeArtefacts = mock[PurgeProjectArtefacts]
    val mockAcquireArtefacts = mock[AcquireProjectArtefacts]

    val acquirer = new DownloadAcquireUpdateProjectArtefactsFromCacheToLocal(mockGetProjectDto,
      mockAcquireSource,
      mockCompileSource,
      mockReadArtefactDetails,
      mockPurgeArtefacts,
      mockAcquireArtefacts)

    describe("When acquiring and installing the details") {

      val dto = GenModel.net.genProjectDto()
      Mockito.when(mockGetProjectDto.apply(Matchers.any[Project], Matchers.any[BuildCompiler]))
        .thenReturn(future(dto))

      val sourceVersion = GenModel.store.genSourceVersion()
      Mockito.when(mockAcquireSource.apply(Matchers.any[Project], Matchers.any[ProjectDto], Matchers.any[Branch]))
        .thenReturn(sourceVersion)

      val artefactDetails = Iterator.continually(GenModel.store.genArtefactDetails())
        .dropWhile(_.version == sourceVersion.value)
        .take(1)
        .toList
        .head

      Mockito.when(mockReadArtefactDetails.apply(Matchers.any[Project], Matchers.any[BuildCompiler], Matchers.any[BuildMode]))
        .thenReturn(artefactDetails)

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
      it("Then the artefact details are read") {
        Mockito.verify(mockReadArtefactDetails).apply(project, compiler, mode)
      }
      it("Then the current artefacts are purged") {
        Mockito.verify(mockPurgeArtefacts).apply(project, compiler, mode)
      }
      it("Then the artefacts are acquired") {
        Mockito.verify(mockAcquireArtefacts).apply(project, compiler, mode, branch, sourceVersion)
      }
    }
  }

  describe("Given a project/compiler/mode to update and local artefacts with the same") {
    val mockGetProjectDto = mock[GetProjectDto]
    val mockAcquireSource = mock[AcquireProjectSource]
    val mockCompileSource = mock[CompileProjectSource]
    val mockReadArtefactDetails = mock[ReadProjectArtefactDetails]
    val mockPurgeArtefacts = mock[PurgeProjectArtefacts]
    val mockAcquireArtefacts = mock[AcquireProjectArtefacts]

    val acquirer = new DownloadAcquireUpdateProjectArtefactsFromCacheToLocal(mockGetProjectDto,
      mockAcquireSource,
      mockCompileSource,
      mockReadArtefactDetails,
      mockPurgeArtefacts,
      mockAcquireArtefacts)

    describe("When acquiring and installing the details") {

      val dto = GenModel.net.genProjectDto()
      Mockito.when(mockGetProjectDto.apply(Matchers.any[Project], Matchers.any[BuildCompiler]))
        .thenReturn(future(dto))

      val sourceVersion = GenModel.store.genSourceVersion()
      Mockito.when(mockAcquireSource.apply(Matchers.any[Project], Matchers.any[ProjectDto], Matchers.any[Branch]))
        .thenReturn(sourceVersion)

      val artefactDetails = new ArtefactDetails(sourceVersion.value, Seq[String]())

      Mockito.when(mockReadArtefactDetails.apply(Matchers.any[Project], Matchers.any[BuildCompiler], Matchers.any[BuildMode]))
        .thenReturn(artefactDetails)

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
      it("Then the artefact details are read") {
        Mockito.verify(mockReadArtefactDetails).apply(project, compiler, mode)
      }
      it("Then the current artefacts are not purged") {
        Mockito.verify(mockPurgeArtefacts, Mockito.never()).apply(project, compiler, mode)
      }
      it("Then the artefacts are not acquired") {
        Mockito.verify(mockAcquireArtefacts, Mockito.never()).apply(project, compiler, mode, branch, sourceVersion)
      }
    }
  }
}
