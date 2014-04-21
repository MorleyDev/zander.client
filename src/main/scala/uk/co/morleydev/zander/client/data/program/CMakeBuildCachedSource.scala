package uk.co.morleydev.zander.client.data.program

import uk.co.morleydev.zander.client.data.ProjectSourceBuild
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.{BuildMode, Project}
import java.io.File
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.data.exception.CMakeBuildFailedException

class CMakeBuildCachedSource(cmakeProgram : String,
                      runner : ProgramRunner,
                      temp : File) extends ProjectSourceBuild {
  override def apply(project : Project, compiler : BuildCompiler, mode : BuildMode) : Unit = {

    val modeConfig = mode match {
      case BuildMode.Debug => "Debug"
      case BuildMode.Release => "Release"
    }

    val exitCode = runner(Seq[String](cmakeProgram, "--build", ".", "--config", modeConfig), temp)
    if (exitCode != 0)
      throw new CMakeBuildFailedException(exitCode)
  }
}
