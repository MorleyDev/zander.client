package uk.co.morleydev.zander.client.test.unit.validator.impl

import org.mockito.{Mockito, Matchers}
import uk.co.morleydev.zander.client.data.CheckArtefactDetailsExist
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.test.gen.GenModel
import uk.co.morleydev.zander.client.test.unit.UnitTest
import uk.co.morleydev.zander.client.validator.exception.NoLocalArtefactsExistException
import uk.co.morleydev.zander.client.validator.impl.ValidateArtefactDetailsExist

class ValidateArtefactDetailsExistTests extends UnitTest {

  describe("Given artefact details exist When validating they exist") {

    val check = mock[CheckArtefactDetailsExist]
    Mockito.when(check.apply(Matchers.any[Project], Matchers.any[BuildCompiler], Matchers.any[BuildMode]))
      .thenReturn(true)
    val validate = new ValidateArtefactDetailsExist(check)

    val project = GenModel.arg.genProject()
    val compiler = GenModel.arg.genCompiler()
    val mode = GenModel.arg.genBuildMode()

    validate.apply(project, compiler, mode)

    it("Then the existance is checked") {
      Mockito.verify(check).apply(project, compiler, mode)
    }
  }

  describe("Given no artefact details exist When validating they exist") {

    val check = mock[CheckArtefactDetailsExist]
    Mockito.when(check.apply(Matchers.any[Project], Matchers.any[BuildCompiler], Matchers.any[BuildMode]))
           .thenReturn(false)
    val validate = new ValidateArtefactDetailsExist(check)

    val thrownException : Throwable = try {
      validate.apply(GenModel.arg.genProject(), GenModel.arg.genCompiler(), GenModel.arg.genBuildMode())
      null
    } catch {
      case e : Throwable => e
    }
    it("Then an exception is thrown") {
      assert(thrownException != null)
    }
    it("Then the expected exception is thrown") {
      assert(thrownException.isInstanceOf[NoLocalArtefactsExistException])
    }
  }
}


