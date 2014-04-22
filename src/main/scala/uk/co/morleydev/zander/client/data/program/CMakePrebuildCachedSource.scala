package uk.co.morleydev.zander.client.data.program

import uk.co.morleydev.zander.client.data.{BuildModeBuildTypeMap, CompilerGeneratorMap, PreBuildProjectSource}
import uk.co.morleydev.zander.client.model.arg.{BuildMode, Project}
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import java.io.File
import uk.co.morleydev.zander.client.data.exception.CMakePreBuildFailedException

class CMakePreBuildCachedSource(cmakeProgram : String,
                                runner : ProgramRunner,
                                cache : File, temp : File,
                                generatorMap : CompilerGeneratorMap,
                                buildTypeMap : BuildModeBuildTypeMap) extends PreBuildProjectSource {

  override def apply(project : Project, compiler : BuildCompiler, mode : BuildMode) : Unit = {

    val command = Seq[String](cmakeProgram,
      new File(cache, project.value + "/source").getAbsolutePath) ++
      generatorMap(compiler) ++
      Seq[String]("-DCMAKE_BUILD_TYPE=" + buildTypeMap(mode),
        "-DCMAKE_INSTALL_PREFIX=" + new File(cache, "%s/%s.%s".format(project.value, compiler, mode)).getAbsolutePath)

    val exitCode = runner.apply(command, temp)
    if ( exitCode != 0 )
      throw new CMakePreBuildFailedException(exitCode)
  }
}
