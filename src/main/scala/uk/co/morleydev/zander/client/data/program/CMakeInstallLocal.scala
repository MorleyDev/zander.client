package uk.co.morleydev.zander.client.data.program

import uk.co.morleydev.zander.client.data.ProjectSourceInstall
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.Compiler.Compiler
import java.io.File

class CMakeInstallLocal(cmakeProgram : String,
                        runner : ProgramRunner,
                        temp : File) extends ProjectSourceInstall {
  override def apply(project : Project, compiler : Compiler) : Unit = {
    runner.apply(Seq[String](cmakeProgram, "--build", ".", "--", "install"), temp)
  }
}
