package uk.co.morleydev.zander.client.test.gen

object GenStringArguments {

  val buildModes = Array[String]("debug", "release")
  val compilers = Array[String]("gnu", "msvc10", "msvc11", "msvc12", "msvc10w64", "msvc11w64", "msvc12w64")
  val operations = Array[String]("install", "purge", "update", "get")

  def genOperation() : String = GenNative.genOneFrom[String](operations)
  def genProject() : String = GenNative.genAlphaNumericString(1, 20)
  def genCompiler() : String = GenNative.genOneFrom(compilers)
  def genBuildMode() : String = GenNative.genOneFrom(buildModes)
}
