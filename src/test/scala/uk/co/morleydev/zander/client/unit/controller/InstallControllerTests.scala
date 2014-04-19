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
import uk.co.morleydev.zander.client.service.ProjectSourceAcquire

class InstallControllerTests extends FunSpec with MockitoSugar {
  describe("Given an install controller") {
    val mockGetProjectDto = mock[GetProjectDto]
    val mockSourceAcquire = mock[ProjectSourceAcquire]
    val mockSourcePrebuild = mock[ProjectSourcePrebuild]
    val mockSourceBuild = mock[ProjectSourceBuild]
    val mockSourceInstall = mock[ProjectSourceInstall]
    val mockArtefactInstall = mock[ProjectArtefactInstall]

    val installController = new InstallController(mockGetProjectDto,
      mockSourceAcquire,
      mockSourcePrebuild,
      mockSourceBuild,
      mockSourceInstall,
      mockArtefactInstall)

    describe("when installing an existing project") {
      val project = GenModel.arg.genProject()
      val compiler = GenNative.genOneFrom(Compiler.values.toSeq)
      val projectDto = GenModel.net.genGitSupportingProjectDto()

      Mockito.when(mockGetProjectDto(Matchers.any[Project](), Matchers.any[Compiler]()))
        .thenReturn(future(projectDto))

      val mode = GenNative.genOneFrom(BuildMode.values.toSeq)
      installController(project,
                        compiler,
                        mode)

      it("Then the expected project is retrieved with the expected compiler") {
        Mockito.verify(mockGetProjectDto)(project, compiler)
      }
      it("Then the source is acquired") {
        Mockito.verify(mockSourceAcquire)(project, projectDto)
      }
      it("Then the source prebuild is ran") {
        Mockito.verify(mockSourcePrebuild)(project, compiler, mode)
      }
      it("Then the source build is ran") {
        Mockito.verify(mockSourceBuild)(project, compiler, mode)
      }
      it("Then the source install is ran") {
        Mockito.verify(mockSourceInstall)(project, compiler)
      }
      it("Then the artefact install is ran") {
        Mockito.verify(mockArtefactInstall)(project, compiler, mode)
      }
    }
  }
}
