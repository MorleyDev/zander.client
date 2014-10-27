package uk.co.morleydev.zander.client.data.program

import java.io.File

import uk.co.morleydev.zander.client.data.DownloadProjectSource
import uk.co.morleydev.zander.client.data.exception.GitDownloadFailedException
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.net.ProjectDto

import scala.concurrent.ExecutionContext

class GitDownloadSourceToCache(gitProgram : String,
                        programRunner : ProgramRunner,
                        programCacheDirectory : File,
                        implicit val executionContext : ExecutionContext = ExecutionContext.Implicits.global)
  extends DownloadProjectSource {
  override def apply(project: Project, dto: ProjectDto): Unit = {

    val workingDirectory = new File(programCacheDirectory, project.value)
    val command = Seq[String](gitProgram, "clone", dto.src.href, "source")
    val responseCode = programRunner(command, workingDirectory)
    if (responseCode != 0)
      throw new GitDownloadFailedException(responseCode)
  }
}
