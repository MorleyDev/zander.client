package uk.co.morleydev.zander.client.data.program

import uk.co.morleydev.zander.client.data.ProjectSourceInstall
import uk.co.morleydev.zander.client.model.arg.{BuildMode, Project}
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import java.io.File
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode

class CMakeInstallCachedSource(cmakeProgram : String,
                        runner : ProgramRunner,
                        temp : File) extends ProjectSourceInstall {
  override def apply(project : Project, compiler : BuildCompiler, mode : BuildMode) : Unit = {

    val config = mode match {
      case BuildMode.Debug => "Debug"
      case BuildMode.Release => "Release"
    }

    runner.apply(Seq[String](cmakeProgram, "--build", ".", "--config", config, "--target", "install"), temp)
  }
}
