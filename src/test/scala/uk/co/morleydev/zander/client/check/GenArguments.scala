package uk.co.morleydev.zander.client.check

import org.scalacheck.{Arbitrary, Gen}
import java.net.URLEncoder

object GenArguments {
  def gen(operation: String) : Gen[Array[String]] = for {
    project <- Gen.alphaStr.map(f => URLEncoder.encode(f, "UTF-8"))
    compiler <-  Gen.oneOf("gnu", "msvc10", "msvc11", "msvc12")
    mode <- Gen.oneOf("debug", "release")
  } yield Array[String](operation, project, compiler, mode)

  def genInstall() = gen("install")
  def genGet() = gen("get")
  def genUpdate() = gen("update")
  def genPurge() = gen("purge")

  def arb(operation : String) : Arbitrary[Array[String]] = Arbitrary(Array[String](operation, "project", "gnu", "debug"))
  implicit val arbInstall = arb("install")
  implicit val arbGet = arb("get")
  implicit val arbUpdate = arb("update")
  implicit val arbPurge = arb("purge")
}
