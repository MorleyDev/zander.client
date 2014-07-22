package uk.co.morleydev.zander.client.test.unit.data.map

import java.io.File

import uk.co.morleydev.zander.client.data.map.GetCachedArtefactsLocation
import uk.co.morleydev.zander.client.test.gen.GenModel
import uk.co.morleydev.zander.client.test.unit.UnitTest

class GetCacheArtefactsLocationTests extends UnitTest {
  describe("Given a cache root") {
    val cachePathFile = new File("some/cache/path")
    val getCacheLocationFromCache = new GetCachedArtefactsLocation(cachePathFile)

    describe("When getting the cache location for artefacts") {
      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()
      val branch = GenModel.arg.genBranch()
      val actual = getCacheLocationFromCache(project, compiler, mode, branch)

      it("Then the expected cache location is returned") {
        assert(actual == new File(cachePathFile, "%s/%s/%s.%s".format(project,branch,compiler,mode)))
      }
    }
  }
}
