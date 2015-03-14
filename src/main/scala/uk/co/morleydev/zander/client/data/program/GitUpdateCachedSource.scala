package uk.co.morleydev.zander.client.data.program

import uk.co.morleydev.zander.client.data.UpdateProjectSource
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.net.ProjectDto
import java.io.File
import uk.co.morleydev.zander.client.data.exception.GitUpdateFailedException

class GitUpdateCachedSource(git : String, cache : File, processFactory : ProgramRunner) extends UpdateProjectSource {
  override def apply(project: Project, dto: ProjectDto): Unit = {
    val checkoutExitCode = processFactory(Seq[String](git, "checkout", "master"), new File(cache, project + "/source"))
    if (checkoutExitCode != 0)
      throw new GitUpdateFailedException(checkoutExitCode)

    val pullExitCode = processFactory(Seq[String](git, "pull"), new File(cache, project + "/source"))
    if (pullExitCode != 0)
      throw new GitUpdateFailedException(pullExitCode)
  }
}
