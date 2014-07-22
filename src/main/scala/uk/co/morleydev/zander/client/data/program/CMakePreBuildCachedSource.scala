package uk.co.morleydev.zander.client.data.program

import java.io.File

import uk.co.morleydev.zander.client.data._
import uk.co.morleydev.zander.client.data.exception.CMakePreBuildFailedException
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.{Branch, Project}

class CMakePreBuildCachedSource(cmakeProgram : String,
                                runner : ProgramRunner,
                                getSourceLocation : GetSourceLocation,
                                getArtefactLocation : GetArtefactsLocation,
                                temp : File,
                                generatorMap : CompilerGeneratorMap,
                                buildTypeMap : BuildModeBuildTypeMap) extends PreBuildProjectSource {

  override def apply(project : Project, compiler : BuildCompiler, mode : BuildMode, branch : Branch) : Unit = {

    val command = Seq[String](cmakeProgram,
      getSourceLocation(project).getAbsolutePath) ++
      generatorMap(compiler) ++
      Seq[String]("-DCMAKE_BUILD_TYPE=" + buildTypeMap(mode),
        "-DCMAKE_INSTALL_PREFIX=" + getArtefactLocation(project, compiler, mode, branch).getAbsolutePath)

    val exitCode = runner.apply(command, temp)
    if ( exitCode != 0 )
      throw new CMakePreBuildFailedException(exitCode)
  }
}
