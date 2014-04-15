package uk.co.morleydev.zander.client.model.arg

object Compiler extends Enumeration {

  type Compiler = Value

  val VisualStudio10 = Value("msvc10")
  val VisualStudio11 = Value("msvc11")
  val VisualStudio12 = Value("msvc12")
  val GnuCxx = Value("gnu")
}
