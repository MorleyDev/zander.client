package uk.co.morleydev.zander.client.data.program

import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.net.ProjectDto
import scala.concurrent.{ExecutionContext, Future, future}

class GitDownloadRemote(gitProgram : String,
                        processBuilderFactory : NativeProcessBuilderFactory,
                        programCacheDirectory : String,
                        implicit val executionContext : ExecutionContext = ExecutionContext.Implicits.global)
  extends GitDownload {
  override def apply(project: Project, dto: ProjectDto) : Future[Unit] = {
    future {
      processBuilderFactory.apply(Seq[String](gitProgram, "clone", dto.git, "source"))
      .directory(programCacheDirectory + "/" + project)
      .start()
      .waitFor()
    }
  }
}
