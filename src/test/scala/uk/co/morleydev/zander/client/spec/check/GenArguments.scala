package uk.co.morleydev.zander.client.check

import org.scalacheck.{Arbitrary, Gen}
import scala.util.Random

object GenArguments {

  private val random = new Random()
  val buildModes = Array[String]("debug", "release")
  val compilers = Array[String]("gnu", "msvc10", "msvc11", "msvc12")
  val operations = Array[String]("install", "purge", "update", "get")

  def genBuildMode() = buildModes(random.nextInt(buildModes.size))
  def genCompiler() = compilers(random.nextInt(compilers.size))
  def genProject() = random.alphanumeric.take(random.nextInt(20)+1).mkString
  def genOperation() = operations(random.nextInt(operations.size))
}
