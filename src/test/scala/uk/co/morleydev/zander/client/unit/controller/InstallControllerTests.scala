package uk.co.morleydev.zander.client.unit.controller

import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.gen.{GenModel, GenNative}
import uk.co.morleydev.zander.client.model.arg.{Project, Operation, Compiler, BuildMode}
import uk.co.morleydev.zander.client.model.arg.Compiler.Compiler
import org.scalatest.mock.MockitoSugar
import org.mockito.{Matchers, Mockito}
import scala.concurrent.future
import scala.concurrent.ExecutionContext.Implicits.global
import uk.co.morleydev.zander.client.model.net.ProjectDto
import uk.co.morleydev.zander.client.controller.InstallController
import uk.co.morleydev.zander.client.data.{CMakePrebuild, GetProject, GitDownload}

class InstallControllerTests extends FunSpec with MockitoSugar {
  describe("Given an install controller") {
    val mockGetProject = mock[GetProject]
    val mockGitDownload = mock[GitDownload]
    val mockCmakePrebuild = mock[CMakePrebuild]
    val installController = new InstallController(mockGetProject, mockGitDownload, mockCmakePrebuild)

    describe("when installing an existing project") {
      val project = GenModel.arg.genProject()
      val compiler = GenNative.genOneFrom(Compiler.values.toSeq)
      val projectDto = GenModel.net.genProjectDto()

      Mockito.when(mockGetProject(Matchers.any[Project](), Matchers.any[Compiler]()))
        .thenReturn(future(projectDto))

      val mode = GenNative.genOneFrom(BuildMode.values.toSeq)
      installController(project,
                        compiler,
                        mode)

      it("Then the expected project is retrieved with the expected compiler") {
        Mockito.verify(mockGetProject)(project, compiler)
      }
      it("Then the git repository is downloaded") {
        Mockito.verify(mockGitDownload)(project, projectDto)
      }
      it("Then the cmake prebuild is ran") {
        Mockito.verify(mockCmakePrebuild)(project, compiler, mode)
      }
    }
  }
}
