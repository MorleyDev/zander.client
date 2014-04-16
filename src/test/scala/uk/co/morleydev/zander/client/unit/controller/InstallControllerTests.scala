package uk.co.morleydev.zander.client.unit.controller

import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.controller.impl.InstallController
import uk.co.morleydev.zander.client.check.GenNative
import uk.co.morleydev.zander.client.model.arg.Operation
import uk.co.morleydev.zander.client.model.arg.Compiler
import uk.co.morleydev.zander.client.model.arg.Compiler.Compiler
import uk.co.morleydev.zander.client.model.arg.BuildMode
import org.scalatest.mock.MockitoSugar
import uk.co.morleydev.zander.client.data.net.GetProject
import org.mockito.{Matchers, Mockito}
import scala.concurrent.future
import scala.concurrent.ExecutionContext.Implicits.global
import uk.co.morleydev.zander.client.model.net.Project

class InstallControllerTests extends FunSpec with MockitoSugar {
  describe("Given an install controller") {
    val mockGetProject = mock[GetProject]
    val installController = new InstallController(mockGetProject)

    describe("when installing an existing project") {
      val projectName = GenNative.genAlphaNumericString(1, 20)
      val compiler = GenNative.genOneFrom(Compiler.values.toSeq)

      Mockito.when(mockGetProject.apply(Matchers.any[String](), Matchers.any[Compiler]()))
        .thenReturn(future(new Project("")))

      installController(GenNative.genOneFrom(Operation.values.toSeq),
                        projectName,
                        compiler,
                        GenNative.genOneFrom(BuildMode.values.toSeq))

      it("Then the expected project is retrieved with the expected compiler") {
        Mockito.verify(mockGetProject)(projectName, compiler)
      }
    }
  }
}
