package uk.co.morleydev.zander.client.model.arg

object BuildCompiler extends Enumeration {

  type BuildCompiler = Value

  val VisualStudio10 = Value("msvc10")
  val VisualStudio11 = Value("msvc11")
  val VisualStudio12 = Value("msvc12")
  val VisualStudio10Win64 = Value("msvc10w64")
  val VisualStudio11Win64 = Value("msvc11w64")
  val VisualStudio12Win64 = Value("msvc12w64")
  val GnuCxx = Value("gnu")
}
