package uk.co.morleydev.zander.client.test.unit.data.map

import java.io.File

import uk.co.morleydev.zander.client.data.map.GetCachedSourceLocation
import uk.co.morleydev.zander.client.test.gen.GenModel
import uk.co.morleydev.zander.client.test.unit.UnitTest

class GetCacheSourceLocationTests extends UnitTest {
  describe("Given a cache root") {
    val cachePathFile = new File("some/cache/path")
    val getCacheLocationFromCache = new GetCachedSourceLocation(cachePathFile)

    describe("When getting the cache location for source") {
      val project = GenModel.arg.genProject()
      val actual = getCacheLocationFromCache(project)

      it("Then the expected cache location is returned") {
        assert(actual == new File(cachePathFile, "%s/src".format(project)))
      }
    }
  }
}
