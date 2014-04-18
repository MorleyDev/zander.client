package uk.co.morleydev.zander.client.data.program

import uk.co.morleydev.zander.client.data.ProjectSourcePrebuild
import uk.co.morleydev.zander.client.model.arg.{BuildMode, Project}
import uk.co.morleydev.zander.client.model.arg.Compiler.Compiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import java.io.File

class CMakePrebuildLocal(cmakeProgram : String,
                         runner : ProgramRunner,
                         cache : File, temp : File) extends ProjectSourcePrebuild {
  override def apply(project : Project, compiler : Compiler, mode : BuildMode) : Unit = {

    val buildType = mode match {
      case BuildMode.Debug => ("Debug", "debug")
      case BuildMode.Release => ("Release", "release")
    }

    val command = Seq[String](cmakeProgram,
      new File(cache, project.value + "/source").getAbsolutePath,
      "-G\"MinGW", "Makefiles\"",
      "-DCMAKE_BUILD_TYPE=" + buildType._1,
      "-DCMAKE_INSTALL_PREFIX=" + new File(cache, project.value + "/gnu." + buildType._2).getAbsolutePath)

    runner.apply(command, temp)
  }
}
