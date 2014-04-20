package uk.co.morleydev.zander.client.data.program

import uk.co.morleydev.zander.client.data.ProjectSourceInstall
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import java.io.File
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode

class CMakeInstallCachedSource(cmakeProgram : String,
                        runner : ProgramRunner,
                        temp : File) extends ProjectSourceInstall {
  override def apply(project : Project, compiler : BuildCompiler, mode : BuildMode) : Unit = {
    runner.apply(Seq[String](cmakeProgram, "--build", ".", "--", "install"), temp)
  }
}
