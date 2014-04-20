package uk.co.morleydev.zander.client.data.program

import uk.co.morleydev.zander.client.data.ProjectSourceBuild
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.Project
import java.io.File

class CMakeBuildCachedSource(cmakeProgram : String,
                      runner : ProgramRunner,
                      temp : File) extends ProjectSourceBuild {
  override def apply(project : Project, compiler : BuildCompiler, mode : BuildMode) : Unit = {
    runner.apply(Seq[String](cmakeProgram, "--build", "."), temp)
  }
}
