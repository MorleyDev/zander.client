package uk.co.morleydev.zander.client.test.unit.controller

import uk.co.morleydev.zander.client.model.OperationArguments
import uk.co.morleydev.zander.client.test.unit.UnitTest
import uk.co.morleydev.zander.client.controller.impl.GetController
import uk.co.morleydev.zander.client.test.gen.GenModel
import uk.co.morleydev.zander.client.data.CheckArtefactDetailsExist
import uk.co.morleydev.zander.client.service.{DownloadAcquireUpdateProjectArtefacts, DownloadAcquireInstallProjectArtefacts}
import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.Project

class GetControllerTests extends UnitTest {
  describe("Given a project/compiler/mode and no installed artefacts") {

    val mockCheckArtefactDetailsExist = mock[CheckArtefactDetailsExist]
    val mockInstallArtefacts = mock[DownloadAcquireInstallProjectArtefacts]
    val getController = new GetController(mockCheckArtefactDetailsExist, mockInstallArtefacts, null)
    describe("When get") {

      Mockito.when(mockCheckArtefactDetailsExist.apply(Matchers.any[Project], Matchers.any[BuildCompiler], Matchers.any[BuildMode]))
             .thenReturn(false)

      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()
      val branch = GenModel.arg.genBranch()

      getController.apply(new OperationArguments(project, compiler, mode, branch))

      it("Then the existence of artefacts is checked") {
        Mockito.verify(mockCheckArtefactDetailsExist).apply(project, compiler, mode)
      }
      it("Then the artefacts are installed") {
        Mockito.verify(mockInstallArtefacts).apply(project, compiler, mode, branch)
      }
    }
  }

  describe("Given a project/compiler/mode and installed artefacts") {

    val mockCheckArtefactDetailsExist = mock[CheckArtefactDetailsExist]
    val mockUpdateArtefacts = mock[DownloadAcquireUpdateProjectArtefacts]
    val getController = new GetController(mockCheckArtefactDetailsExist, null, mockUpdateArtefacts)
    describe("When get") {

      Mockito.when(mockCheckArtefactDetailsExist.apply(Matchers.any[Project], Matchers.any[BuildCompiler], Matchers.any[BuildMode]))
        .thenReturn(true)

      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()
      val branch = GenModel.arg.genBranch()

      getController.apply(new OperationArguments(project, compiler, mode, branch))

      it("Then the existence of artefacts is checked") {
        Mockito.verify(mockCheckArtefactDetailsExist).apply(project, compiler, mode)
      }
      it("Then the artefacts are updated") {
        Mockito.verify(mockUpdateArtefacts).apply(project, compiler, mode, branch)
      }
    }
  }
}
