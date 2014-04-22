package uk.co.morleydev.zander.client.test.gen

import uk.co.morleydev.zander.client.model.arg.{Project, BuildMode, Operation, BuildCompiler}
import uk.co.morleydev.zander.client.model.arg.Operation.Operation
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.net.ProjectDto
import scala.util.Random
import java.net.URL
import uk.co.morleydev.zander.client.model.store.{ArtefactDetails, SourceVersion}

object GenModel {

  private val random = new Random

  object arg {
    def genOperation(): Operation = GenNative.genOneFrom(Operation.values.toSeq)

    def genCompiler(): BuildCompiler = GenNative.genOneFrom(BuildCompiler.values.toSeq)

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

    def genGitSupportingProjectDto() : ProjectDto = new ProjectDto(genGitUrl())

    def genProjectDto() : ProjectDto = new ProjectDto(genGitUrl())
  }

  object store {
    private def genArtefactFiles : Seq[String] =
      GenNative.genSequence(1, 100, () => "include/" + GenNative.genAlphaNumericString(3, 100)) ++
        GenNative.genSequence(1, 100, () => "bin/" + GenNative.genAlphaNumericString(3, 100)) ++
        GenNative.genSequence(1, 100, () => "lib/" + GenNative.genAlphaNumericString(3, 100))

    def genSourceVersion() : SourceVersion =
      new SourceVersion(GenNative.genAlphaNumericString(3, 100))

    def genArtefactDetails() : ArtefactDetails =
      new ArtefactDetails(GenNative.genAlphaNumericString(3, 100), genArtefactFiles)
  }
}
