package uk.co.morleydev.zander.client.unit.model.store

import org.scalatest.FunSpec
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import uk.co.morleydev.zander.client.model.store.SourceVersion
import org.scalacheck.Gen

class SourceVersionTests extends FunSpec with GeneratorDrivenPropertyChecks {

  val versionStringGenerator : Gen[String] = Gen.alphaStr
  val versionGenerator : Gen[SourceVersion] = for { n <- versionStringGenerator } yield new SourceVersion(n)

  describe("Given a source version when getting the value") {
    it("Then the value is as expected") {
      forAll(versionStringGenerator) {
        version : String =>
          assert(new SourceVersion(version).value == version)
          assert(new SourceVersion(version).toString == version)
      }
    }
  }

  describe("Given two different source versions when comparing the versions") {
    it("Then the versions are not equal") {
      forAll(versionGenerator, versionGenerator) {
        (s1 : SourceVersion, s2 : SourceVersion) =>
          whenever(s1.value != s2.value) {
            assert(s1 != s2)
            assert(!(s1 == s2))
          }
      }
    }
  }

  describe("Given two equal source versions when comparing the versions") {
    it("Then the versions are equal") {

      forAll(versionStringGenerator) {
        (s : String) =>
          assert(new SourceVersion(s) == new SourceVersion(s))
          assert(!(new SourceVersion(s) != new SourceVersion(s)))
      }
    }
  }
}
