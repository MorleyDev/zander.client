package uk.co.morleydev.zander.client.unit.service.impl

import org.scalatest.FunSpec
import org.scalatest.mock.MockitoSugar
import uk.co.morleydev.zander.client.service.impl.CachedSourceCompile
import uk.co.morleydev.zander.client.data.{ProjectSourceInstall, ProjectSourceBuild, ProjectSourcePrebuild}
import uk.co.morleydev.zander.client.gen.GenModel
import org.mockito.Mockito

class CachedSourceCompileTests extends FunSpec with MockitoSugar {
  describe("Given a prebuild, build and install") {

    val mockPrebuild = mock[ProjectSourcePrebuild]
    val mockBuild = mock[ProjectSourceBuild]
    val mockInstall = mock[ProjectSourceInstall]

    val cachedSourceCompile = new CachedSourceCompile(mockPrebuild,
                                                      mockBuild,
                                                      mockInstall)

    describe("When compiling artefacts") {
      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()

      cachedSourceCompile.apply(project, compiler, mode)

      it("Then the source is prebuilt") {
        Mockito.verify(mockPrebuild).apply(project, compiler, mode)
      }
      it("Then the source is built") {
        Mockito.verify(mockBuild).apply(project, compiler, mode)
      }
      it("Then the source is installed to the cache") {
        Mockito.verify(mockInstall).apply(project, compiler, mode)
      }
    }
  }
}
