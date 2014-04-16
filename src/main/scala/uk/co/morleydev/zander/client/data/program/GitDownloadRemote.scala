package uk.co.morleydev.zander.client.data.program

import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.net.ProjectDto
import scala.concurrent.{ExecutionContext, Future, future}
import uk.co.morleydev.zander.client.util.Log
import uk.co.morleydev.zander.client.data.{NativeProcessBuilderFactory, GitDownload}
import java.io.{File, InputStreamReader, BufferedReader}
import scala.io.Source

class GitDownloadRemote(gitProgram : String,
                        processBuilderFactory : NativeProcessBuilderFactory,
                        programCacheDirectory : File,
                        implicit val executionContext : ExecutionContext = ExecutionContext.Implicits.global)
  extends GitDownload {
  override def apply(project: Project, dto: ProjectDto): Future[Unit] = {

    val workingDirectory = new File(programCacheDirectory, project.value)
    Log("Invoking git for", dto.git, workingDirectory)

    future {
      processBuilderFactory(Seq[String](gitProgram, "clone", dto.git, "source"))
        .directory(workingDirectory)
        .start()
    }.map(process => {
      Log("Git process started")
      Source.fromInputStream(process.getInputStream).getLines().foreach(println(_))
      Source.fromInputStream(process.getErrorStream).getLines().foreach(println(_))

      val responseCode = process.waitFor()
      Log("Git exited with response code", responseCode)
    })
  }
}
