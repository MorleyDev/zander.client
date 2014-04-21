package uk.co.morleydev.zander.client.data.map

import uk.co.morleydev.zander.client.data.CompilerGeneratorMap
import uk.co.morleydev.zander.client.model.arg.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler

object CmakeCompilerGeneratorMap extends CompilerGeneratorMap {
  override def apply(compiler: BuildCompiler): Seq[String] =
    (compiler match {
      case BuildCompiler.GnuCxx => "-G\"MinGW Makefiles\""
      case BuildCompiler.VisualStudio10 => "-G\"Visual Studio 10\""
      case BuildCompiler.VisualStudio11 => "-G\"Visual Studio 11\""
      case BuildCompiler.VisualStudio12 => "-G\"Visual Studio 12\""
    }).split(" ")
}
