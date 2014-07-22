package uk.co.morleydev.zander.client.data.program

import java.io.File

import uk.co.morleydev.zander.client.data.CheckoutProjectSource
import uk.co.morleydev.zander.client.data.exception.GitCheckoutFailedException
import uk.co.morleydev.zander.client.model.arg.{Branch, Project}

import scala.concurrent.ExecutionContext

class GitCheckoutCachedSource(gitProgram : String,
                              programRunner : ProgramRunner,
                              programCacheDirectory : File,
                              implicit val executionContext : ExecutionContext = ExecutionContext.Implicits.global) extends CheckoutProjectSource {
  override def apply(project: Project, branch: Branch): Unit = {

    val workingDirectory = new File(programCacheDirectory, "%s/%s".format(project.value, "source"))
    val command = Seq[String](gitProgram, "checkout", branch.toString)
    val responseCode = programRunner(command, workingDirectory)
    if (responseCode != 0)
      throw new GitCheckoutFailedException(responseCode)
  }
}
