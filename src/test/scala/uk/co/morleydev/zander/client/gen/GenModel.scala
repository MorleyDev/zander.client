package uk.co.morleydev.zander.client.gen

import uk.co.morleydev.zander.client.model.arg.{Project, BuildMode, Operation, Compiler}
import uk.co.morleydev.zander.client.model.arg.Operation.Operation
import uk.co.morleydev.zander.client.model.arg.Compiler.Compiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.net.ProjectDto
import scala.util.Random
import java.net.URL

object GenModel {

  private val random = new Random

  object arg {
    def genOperation(): Operation = GenNative.genOneFrom(Operation.values.toSeq)

    def genCompiler(): Compiler = GenNative.genOneFrom(Compiler.values.toSeq)

    def genBuildMode(): BuildMode = GenNative.genOneFrom(BuildMode.values.toSeq)

    def genProject(): Project =
      new Project(GenNative.genStringContaining(1, 20,
        GenNative.alphaNumericCharacters ++ Seq[Char]('_', '-', '.')))
  }

  object net {
    def genGitUrl() : String = {
      val user = GenNative.genAlphaNumericString(3, 10)
      val project = GenNative.genAlphaNumericString(3, 10)
      if (random.nextBoolean())
        "git://%s@%s/%s".format(user, GenNative.genAlphaNumericString(3, 10), project)
      else
        new URL(GenNative.genHttpUrl(), "%s/%s".format(user, project)).toString
    }

    def genProjectDto() : ProjectDto = new ProjectDto(genGitUrl())
  }
}
