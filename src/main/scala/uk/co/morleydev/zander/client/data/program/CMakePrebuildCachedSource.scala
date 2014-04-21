package uk.co.morleydev.zander.client.data.program

import uk.co.morleydev.zander.client.data.{CompilerGeneratorMap, ProjectSourcePrebuild}
import uk.co.morleydev.zander.client.model.arg.{BuildMode, Project}
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import java.io.File

class CMakePrebuildCachedSource(cmakeProgram : String,
                                runner : ProgramRunner,
                                cache : File, temp : File,
                                generatorMap : CompilerGeneratorMap) extends ProjectSourcePrebuild {
  override def apply(project : Project, compiler : BuildCompiler, mode : BuildMode) : Unit = {

    val buildType = mode match {
      case BuildMode.Debug => ("Debug", "debug")
      case BuildMode.Release => ("Release", "release")
    }

    val command = Seq[String](cmakeProgram,
      new File(cache, project.value + "/source").getAbsolutePath) ++
      generatorMap(compiler) ++
      Seq[String]("-DCMAKE_BUILD_TYPE=" + buildType._1,
        "-DCMAKE_INSTALL_PREFIX=" + new File(cache, "%s/%s.%s".format(project.value, compiler, buildType._2)).getAbsolutePath)

    runner.apply(command, temp)
  }
}
