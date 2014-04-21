package uk.co.morleydev.zander.client.data.program

import java.io.File
import uk.co.morleydev.zander.client.data.ProjectSourceInstall
import uk.co.morleydev.zander.client.data.exception.CMakeInstallFailedException
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.{BuildMode, Project}

class CMakeInstallCachedSource(cmakeProgram : String,
                        runner : ProgramRunner,
                        temp : File) extends ProjectSourceInstall {

  private val modeCMakeMap = Map[BuildMode, String](
    BuildMode.Debug -> "Debug",
    BuildMode.Release -> "Release"
  )

  override def apply(project : Project, compiler : BuildCompiler, mode : BuildMode) : Unit = {

    val command = Seq[String](cmakeProgram, "--build", ".", "--config", modeCMakeMap(mode), "--target", "install")
    val exitCode = runner.apply(command, temp)
    if (exitCode != 0)
      throw new CMakeInstallFailedException(exitCode)
  }
}
