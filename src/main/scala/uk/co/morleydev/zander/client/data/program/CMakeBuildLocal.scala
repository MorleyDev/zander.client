package uk.co.morleydev.zander.client.data.program

import uk.co.morleydev.zander.client.data.CMakeBuild
import uk.co.morleydev.zander.client.model.arg.Compiler.Compiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.Project
import java.io.File

class CMakeBuildLocal(cmakeProgram : String,
                      runner : ProgramRunner,
                      temp : File) extends CMakeBuild {
  override def apply(project : Project, compiler : Compiler, mode : BuildMode) : Unit = {
    runner.apply(Seq[String](cmakeProgram, "--build", "."), temp)
  }
}
