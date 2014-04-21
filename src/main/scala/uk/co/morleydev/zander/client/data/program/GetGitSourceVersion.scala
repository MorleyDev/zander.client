package uk.co.morleydev.zander.client.data.program

import uk.co.morleydev.zander.client.data.{NativeProcessBuilderFactory, GetProjectSourceVersion}
import uk.co.morleydev.zander.client.model.arg.Project
import java.io.File
import scala.io.Source
import uk.co.morleydev.zander.client.model.store.SourceVersion
import uk.co.morleydev.zander.client.data.exception.GitVersionCheckFailedException

class GetGitSourceVersion(git : String,
                          cache : File,
                          processFactory : NativeProcessBuilderFactory) extends GetProjectSourceVersion {
  override def apply(project: Project): SourceVersion = {
    val process = processFactory(Seq[String](git, "rev-parse", "HEAD"))
      .directory(new File(cache, project.value + "/source"))
      .start()

    val version = Source.fromInputStream(process.getInputStream).getLines().mkString("\n")
    val exitCode = process.waitFor()
    if (exitCode != 0)
      throw new GitVersionCheckFailedException(exitCode)

    new SourceVersion(version)
  }
}
