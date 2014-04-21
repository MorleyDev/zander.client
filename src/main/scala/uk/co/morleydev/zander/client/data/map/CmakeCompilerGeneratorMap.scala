package uk.co.morleydev.zander.client.data.map

import uk.co.morleydev.zander.client.data.CompilerGeneratorMap
import uk.co.morleydev.zander.client.model.arg.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler

object CmakeCompilerGeneratorMap extends CompilerGeneratorMap {

  private val buildCompilerGeneratorMap = Map[BuildCompiler, String](
    BuildCompiler.GnuCxx -> "-G\"MinGW Makefiles\"",
    BuildCompiler.VisualStudio10 -> "-G\"Visual Studio 10\"",
    BuildCompiler.VisualStudio11 -> "-G\"Visual Studio 11\"",
    BuildCompiler.VisualStudio12 -> "-G\"Visual Studio 12\""
  )

  override def apply(compiler: BuildCompiler): Seq[String] =
    buildCompilerGeneratorMap(compiler).split(" ")
}
