package uk.co.morleydev.zander.client.unit.controller

import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.gen.{GenModel, GenNative}
import uk.co.morleydev.zander.client.model.arg.{Project, Compiler, BuildMode}
import uk.co.morleydev.zander.client.model.arg.Compiler.Compiler
import org.scalatest.mock.MockitoSugar
import org.mockito.{Matchers, Mockito}
import scala.concurrent.future
import scala.concurrent.ExecutionContext.Implicits.global
import uk.co.morleydev.zander.client.controller.InstallController
import uk.co.morleydev.zander.client.data._
import uk.co.morleydev.zander.client.service.{ProjectArtefactAcquire, ProjectSourceCompile, ProjectSourceAcquire}
import uk.co.morleydev.zander.client.model.net.ProjectDto

class InstallControllerTests extends FunSpec with MockitoSugar {
  describe("Given an install controller") {
    val mockGetProjectDto = mock[GetProjectDto]
    val mockSourceAcquire = mock[ProjectSourceAcquire]
    val mockSourceCompile = mock[ProjectSourceCompile]
    val mockArtefactAcquire = mock[ProjectArtefactAcquire]

    val installController = new InstallController(mockGetProjectDto,
      mockSourceAcquire,
      mockSourceCompile,
      mockArtefactAcquire)

    describe("when installing an existing project") {

      val sourceVersion = GenModel.store.genSourceVersion()
      Mockito.when(mockSourceAcquire.apply(Matchers.any[Project], Matchers.any[ProjectDto]))
             .thenReturn(sourceVersion)

      val project = GenModel.arg.genProject()
      val compiler = GenNative.genOneFrom(Compiler.values.toSeq)
      val projectDto = GenModel.net.genGitSupportingProjectDto()

      Mockito.when(mockGetProjectDto(Matchers.any[Project](), Matchers.any[Compiler]()))
        .thenReturn(future(projectDto))

      val mode = GenNative.genOneFrom(BuildMode.values.toSeq)
      installController(project, compiler, mode)

      it("Then the expected project is retrieved with the expected compiler") {
        Mockito.verify(mockGetProjectDto)(project, compiler)
      }
      it("Then the source is acquired") {
        Mockito.verify(mockSourceAcquire)(project, projectDto)
      }
      it("Then the source is compiled") {
        Mockito.verify(mockSourceCompile)(project, compiler, mode)
      }
      it("Then the local artefacts are acquire") {
        Mockito.verify(mockArtefactAcquire)(project, compiler, mode, sourceVersion)
      }
    }
  }
}
