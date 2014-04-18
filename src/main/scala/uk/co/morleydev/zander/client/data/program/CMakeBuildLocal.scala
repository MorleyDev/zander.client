package uk.co.morleydev.zander.client.data.program

import uk.co.morleydev.zander.client.data.ProjectSourceBuild
import uk.co.morleydev.zander.client.model.arg.Compiler.Compiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.Project
import java.io.File

class CMakeBuildLocal(cmakeProgram : String,
                      runner : ProgramRunner,
                      temp : File) extends ProjectSourceBuild {
  override def apply(project : Project, compiler : Compiler, mode : BuildMode) : Unit = {
    runner.apply(Seq[String](cmakeProgram, "--build", "."), temp)
  }
}
