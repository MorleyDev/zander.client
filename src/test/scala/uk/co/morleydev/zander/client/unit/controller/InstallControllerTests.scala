package uk.co.morleydev.zander.client.unit.controller

import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.gen.{GenModel, GenNative}
import uk.co.morleydev.zander.client.model.arg.{Project, BuildCompiler, BuildMode}
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import org.scalatest.mock.MockitoSugar
import org.mockito.{Matchers, Mockito}
import scala.concurrent.future
import scala.concurrent.ExecutionContext.Implicits.global
import uk.co.morleydev.zander.client.controller.InstallController
import uk.co.morleydev.zander.client.data._
import uk.co.morleydev.zander.client.service.{ProjectArtefactAcquire, ProjectSourceCompile, ProjectSourceAcquire}
import uk.co.morleydev.zander.client.model.net.ProjectDto
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.BuildMode
import org.mockito.stubbing.Answer
import uk.co.morleydev.zander.client.model.store.ArtefactDetails
import org.mockito.invocation.InvocationOnMock
import java.io.FileNotFoundException
import uk.co.morleydev.zander.client.controller.exception.LocalArtefactsAlreadyExistException

class InstallControllerTests extends FunSpec with MockitoSugar {
  describe("Given an install controller") {
    val mockArtefactDetailsReader = mock[ProjectArtefactDetailsReader]
    val mockGetProjectDto = mock[GetProjectDto]
    val mockSourceAcquire = mock[ProjectSourceAcquire]
    val mockSourceCompile = mock[ProjectSourceCompile]
    val mockArtefactAcquire = mock[ProjectArtefactAcquire]

    val installController = new InstallController(mockArtefactDetailsReader,
      mockGetProjectDto,
      mockSourceAcquire,
      mockSourceCompile,
      mockArtefactAcquire)

    describe("when installing a project and no local artefacts exist") {

      val sourceVersion = GenModel.store.genSourceVersion()
      Mockito.when(mockSourceAcquire.apply(Matchers.any[Project], Matchers.any[ProjectDto]))
             .thenReturn(sourceVersion)

      Mockito.when(mockArtefactDetailsReader.apply(Matchers.any[Project](), Matchers.any[BuildCompiler](), Matchers.any[BuildMode]))
             .thenAnswer(new Answer[ArtefactDetails] {
        override def answer(invocation: InvocationOnMock): ArtefactDetails = {
          throw new FileNotFoundException()
        }
      })

      val projectDto = GenModel.net.genGitSupportingProjectDto()
      Mockito.when(mockGetProjectDto(Matchers.any[Project](), Matchers.any[BuildCompiler]()))
        .thenReturn(future(projectDto))

      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()

      installController(project, compiler, mode)

      it("Then the artefact details are attempted to be read") {
        Mockito.verify(mockArtefactDetailsReader).apply(project, compiler, mode)
      }
      it("Then the expected project is retrieved with the expected compiler") {
        Mockito.verify(mockGetProjectDto).apply(project, compiler)
      }
      it("Then the source is acquired") {
        Mockito.verify(mockSourceAcquire).apply(project, projectDto)
      }
      it("Then the source is compiled") {
        Mockito.verify(mockSourceCompile).apply(project, compiler, mode, sourceVersion)
      }
      it("Then the local artefacts are acquire") {
        Mockito.verify(mockArtefactAcquire).apply(project, compiler, mode, sourceVersion)
      }
    }
  }

  describe("Given an install controller") {
    val mockArtefactDetailsReader = mock[ProjectArtefactDetailsReader]
    val installController = new InstallController(mockArtefactDetailsReader,
      null,
      null,
      null,
      null)

    describe("when installing a project and local artefacts exist") {

      Mockito.when(mockArtefactDetailsReader.apply(Matchers.any[Project](), Matchers.any[BuildCompiler](), Matchers.any[BuildMode]))
        .thenReturn(new ArtefactDetails(GenModel.store.genSourceVersion().value))

      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()

      val thrownException : RuntimeException = try {
        installController(project, compiler, mode)
        null
      } catch {
        case t : RuntimeException => t
      }

      it("Then the artefact details are attempted to be read") {
        Mockito.verify(mockArtefactDetailsReader).apply(project, compiler, mode)
      }
      it("Then an exception is thrown") {
        assert(thrownException != null)
      }
      it("Then the expected exception is thrown") {
        assert(thrownException.isInstanceOf[LocalArtefactsAlreadyExistException])
      }
    }
  }
}
