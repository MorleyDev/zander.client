package uk.co.morleydev.zander.client.test.gen

import scala.util.Random

object GenStringArguments {

  private val validProjectCharacters = GenNative.alphaNumericCharacters ++ Seq[Char]('.', '_', '-')
  private val invalidProjectCharacters = (0.toChar to 255.toChar).diff(validProjectCharacters)

  val buildModes = Array[String]("debug", "release")
  val compilers = Array[String](
    "unix", "mingw", "msys",
    "borland",
    "nmake", "nmake-jom",
    "msvc10", "msvc11", "msvc12",
    "msvc10w64", "msvc11w64", "msvc12w64")

  val operations = Array[String]("install", "purge", "update", "get")

  def genOperation() : String = GenNative.genOneFrom[String](operations)
  def genProject() : String = GenNative.genStringContaining(1, 20, validProjectCharacters)
  def genCompiler() : String = GenNative.genOneFrom(compilers)
  def genBuildMode() : String = GenNative.genOneFrom(buildModes)

  def genArray() : Array[String] = Array[String](genOperation(), genProject(), genCompiler(), genBuildMode())

  def genInvalidProjectWithBannedCharacters() : String =
    Iterator.continually(GenNative.genStringContaining(1, 20, (0 to 255).map(_.toChar)))
      .filter(c => c.count(invalidProjectCharacters.contains(_)) > 1)
      .take(1)
      .toSeq
      .head

  def genInvalidProjectWithTooLargeLength() : String =
    GenNative.genStringContaining(21, 100, validProjectCharacters)

  def genInvalidProject() : String =
    if (Random.nextBoolean())
      genInvalidProjectWithBannedCharacters()
    else
      genInvalidProjectWithTooLargeLength()

  def genInvalidOperation() : String =
    GenNative.genAlphaNumericStringExcluding(1, 100, operations)

  def genInvalidCompiler() : String =
    GenNative.genAlphaNumericStringExcluding(1, 100, compilers)

  def genInvalidBuildMode() : String =
    GenNative.genAlphaNumericStringExcluding(1, 100, buildModes)
}
