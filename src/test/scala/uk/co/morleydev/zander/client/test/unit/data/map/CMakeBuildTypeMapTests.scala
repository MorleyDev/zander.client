package uk.co.morleydev.zander.client.test.unit.data.map

import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.model.arg.BuildMode
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.data.map.CMakeBuildModeBuildTypeMap

class CMakeBuildTypeMapTests extends FunSpec {

  private def testCase(buildMode : BuildMode, buildType : String) {

    describe("Given a Build Type Build Mode map") {
      describe("When mapping %s to %s".format(buildMode, buildType)) {
        val result = CMakeBuildModeBuildTypeMap(buildMode)
        it("Then the expected result is returned") {
          assert(result == buildType)
        }
      }
    }
  }
  testCase(BuildMode.Debug, "Debug")
  testCase(BuildMode.Release, "Release")
}
