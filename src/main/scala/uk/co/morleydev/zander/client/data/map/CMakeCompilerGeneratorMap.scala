package uk.co.morleydev.zander.client.data.map

import uk.co.morleydev.zander.client.data.CompilerGeneratorMap
import uk.co.morleydev.zander.client.model.arg.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler

class CMakeCompilerGeneratorMap(isWindows : Boolean) extends CompilerGeneratorMap {

  private val buildCompilerGeneratorMap = Map[BuildCompiler, String](
    BuildCompiler.Mingw -> "MinGW Makefiles",
    BuildCompiler.Unix -> "Unix Makefiles",
    BuildCompiler.Msys -> "MSYS Makefiles",
    BuildCompiler.Borland -> "Borland Makefiles",
    BuildCompiler.NMake -> "NMake Makefiles",
    BuildCompiler.NMakeJom -> "NMake Makefiles JOM",
    BuildCompiler.VisualStudio10 -> "Visual Studio 10",
    BuildCompiler.VisualStudio11 -> "Visual Studio 11",
    BuildCompiler.VisualStudio12 -> "Visual Studio 12",
    BuildCompiler.VisualStudio10Win64 -> "Visual Studio 10 Win64",
    BuildCompiler.VisualStudio11Win64 -> "Visual Studio 11 Win64",
    BuildCompiler.VisualStudio12Win64 -> "Visual Studio 12 Win64"
  )

  override def apply(compiler: BuildCompiler): Seq[String] =
    if (isWindows)
      ("-G\"" + buildCompilerGeneratorMap(compiler) + "\"").split(' ')
    else
      Seq("-G", buildCompilerGeneratorMap(compiler))
}
