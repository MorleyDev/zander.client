package uk.co.morleydev.zander.client.unit.controller

import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.controller.impl.InstallController
import uk.co.morleydev.zander.client.gen.{GenModel, GenNative}
import uk.co.morleydev.zander.client.model.arg.{Project, Operation, Compiler, BuildMode}
import uk.co.morleydev.zander.client.model.arg.Compiler.Compiler
import org.scalatest.mock.MockitoSugar
import uk.co.morleydev.zander.client.data.net.GetProject
import org.mockito.{Matchers, Mockito}
import scala.concurrent.future
import scala.concurrent.ExecutionContext.Implicits.global
import uk.co.morleydev.zander.client.data.program.GitDownload
import uk.co.morleydev.zander.client.model.net.ProjectDto

class InstallControllerTests extends FunSpec with MockitoSugar {
  describe("Given an install controller") {
    val mockGetProject = mock[GetProject]
    val mockGitDownload = mock[GitDownload]
    val installController = new InstallController(mockGetProject, mockGitDownload)

    describe("when installing an existing project") {
      val project = GenModel.arg.genProject()
      val compiler = GenNative.genOneFrom(Compiler.values.toSeq)
      val projectDto = GenModel.net.genProjectDto()

      Mockito.when(mockGetProject(Matchers.any[Project](), Matchers.any[Compiler]()))
        .thenReturn(future(projectDto))
      Mockito.when(mockGitDownload(Matchers.any[Project], Matchers.any[ProjectDto]))
        .thenReturn(future({ }))

      installController(GenNative.genOneFrom(Operation.values.toSeq),
                        project,
                        compiler,
                        GenNative.genOneFrom(BuildMode.values.toSeq))

      it("Then the expected project is retrieved with the expected compiler") {
        Mockito.verify(mockGetProject)(project, compiler)
      }
      it("Then the git repository is downloaded") {
        Mockito.verify(mockGitDownload)(project, projectDto)
      }
    }
  }
}
