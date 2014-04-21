package uk.co.morleydev.zander.client.data.map

import uk.co.morleydev.zander.client.data.CompilerGeneratorMap
import uk.co.morleydev.zander.client.model.arg.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler

object CMakeCompilerGeneratorMap extends CompilerGeneratorMap {

  private val buildCompilerGeneratorMap = Map[BuildCompiler, String](
    BuildCompiler.GnuCxx -> "-G\"MinGW Makefiles\"",
    BuildCompiler.VisualStudio10 -> "-G\"Visual Studio 10\"",
    BuildCompiler.VisualStudio11 -> "-G\"Visual Studio 11\"",
    BuildCompiler.VisualStudio12 -> "-G\"Visual Studio 12\"",
    BuildCompiler.VisualStudio10Win64 -> "-G\"Visual Studio 10 Win64\"",
    BuildCompiler.VisualStudio11Win64 -> "-G\"Visual Studio 11 Win64\"",
    BuildCompiler.VisualStudio12Win64 -> "-G\"Visual Studio 12 Win64\""
  )

  override def apply(compiler: BuildCompiler): Seq[String] =
    buildCompilerGeneratorMap(compiler).split(" ")
}
