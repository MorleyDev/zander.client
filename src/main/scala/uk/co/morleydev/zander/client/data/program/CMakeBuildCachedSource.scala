package uk.co.morleydev.zander.client.data.program

import java.io.File
import uk.co.morleydev.zander.client.data.ProjectSourceBuild
import uk.co.morleydev.zander.client.data.exception.CMakeBuildFailedException
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.{BuildMode, Project}

class CMakeBuildCachedSource(cmakeProgram : String,
                      runner : ProgramRunner,
                      temp : File) extends ProjectSourceBuild {

  private val modeCMakeMap = Map[BuildMode, String](
    BuildMode.Debug -> "Debug",
    BuildMode.Release -> "Release"
  )

  override def apply(project: Project, compiler: BuildCompiler, mode: BuildMode): Unit = {

    val exitCode = runner(Seq[String](cmakeProgram, "--build", ".", "--config", modeCMakeMap(mode)), temp)
    if (exitCode != 0)
      throw new CMakeBuildFailedException(exitCode)
  }
}
