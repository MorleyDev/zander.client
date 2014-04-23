package uk.co.morleydev.zander.client.test.unit.data.map

import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import uk.co.morleydev.zander.client.data.map.SplitJsonFileNameToProjectDetails
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.{BuildCompiler, BuildMode, Project}
import uk.co.morleydev.zander.client.test.gen.GenModel
import uk.co.morleydev.zander.client.test.unit.UnitTest

class SplitJsonFileNameToProjectDetailsTests extends UnitTest with GeneratorDrivenPropertyChecks {

  implicit override val generatorDrivenConfig = PropertyCheckConfig(minSize = 10, maxSize = 20)

  val projectGenerator : Gen[Project] = Gen.oneOf(Iterator.continually(GenModel.arg.genProject()).take(100).toSeq)
  val compilerGenerator : Gen[BuildCompiler] = Gen.oneOf(BuildCompiler.values.toSeq)
  val buildModeGenerator : Gen[BuildMode] = Gen.oneOf(BuildMode.values.toSeq)

  describe("Given a json filename when splitting to separate details") {
    it("Then the expected details are returned") {

      forAll(projectGenerator, compilerGenerator, buildModeGenerator) {
        (project, compiler, mode) =>

          val filename = "%s.%s.%s.json".format(project, compiler, mode)
          val details = SplitJsonFileNameToProjectDetails(filename)
          assert(details._1 == project)
          assert(details._2 == compiler)
          assert(details._3 == mode)
      }
    }
  }

  describe("Given a json filename when invalid compiler") {
    it("Then a NoSuchElementException is thrown") {

      forAll(projectGenerator, buildModeGenerator) {
        (project, mode) =>
          val filename = "%s.invalid.%s.json".format(project, mode)
          val thrownException : Throwable = try {
            SplitJsonFileNameToProjectDetails(filename)
            null
          } catch {
            case e : Throwable => e
          }

          assert(thrownException != null)
          assert(thrownException.isInstanceOf[NoSuchElementException])
      }
    }
  }

  describe("Given a json filename when invalid mode") {
    it("Then a NoSuchElementException is thrown") {

      forAll(projectGenerator, compilerGenerator) {
        (project, compiler) =>
          val filename = "%s.%s.invalid.json".format(project, compiler)
          val thrownException : Throwable = try {
            SplitJsonFileNameToProjectDetails(filename)
            null
          } catch {
            case e : Throwable => e
          }

          assert(thrownException != null)
          assert(thrownException.isInstanceOf[NoSuchElementException])
      }
    }
  }

  describe("Given a json filename when invalid project") {
    it("Then an IllegalArgumentException is thrown") {

      forAll(compilerGenerator, buildModeGenerator) {
        (compiler, mode) =>
          val filename = "+++.%s.%s.json".format(compiler, mode)
          val thrownException : Throwable = try {
            SplitJsonFileNameToProjectDetails(filename)
            null
          } catch {
            case e : Throwable => e
          }

          assert(thrownException != null)
          assert(thrownException.isInstanceOf[IllegalArgumentException])
      }
    }
  }
}
