package uk.co.morleydev.zander.client.data.program

import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.net.ProjectDto
import scala.concurrent.{ExecutionContext, Future, future}
import uk.co.morleydev.zander.client.util.Log
import uk.co.morleydev.zander.client.data.{NativeProcessBuilderFactory, ProjectSourceDownload}
import java.io.{File, InputStreamReader, BufferedReader}
import scala.io.Source
import uk.co.morleydev.zander.client.data.exception.GitDownloadFailedException

class GitDownloadSourceToCache(gitProgram : String,
                        programRunner : ProgramRunner,
                        programCacheDirectory : File,
                        implicit val executionContext : ExecutionContext = ExecutionContext.Implicits.global)
  extends ProjectSourceDownload {
  override def apply(project: Project, dto: ProjectDto): Unit = {

    val workingDirectory = new File(programCacheDirectory, project.value)
    val command = Seq[String](gitProgram, "clone", dto.git, "source")
    val responseCode = programRunner(command, workingDirectory)
    if (responseCode != 0)
      throw new GitDownloadFailedException(responseCode)
  }
}
