package uk.co.morleydev.zander.client.unit.data.program

import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.data.program.{NativeProcessBuilder, NativeProcessBuilderFactory, GitDownloadRemote}
import org.scalatest.mock.MockitoSugar
import uk.co.morleydev.zander.client.gen.{GenNative, GenModel}
import org.mockito.{Matchers, Mockito}
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.duration.SECONDS

class GitDownloadRemoteTests extends FunSpec with MockitoSugar {

  describe("Given a GitDownloadRemote") {
    val mockProcess = mock[Process]

    val mockProcessBuilder = mock[NativeProcessBuilder]
    Mockito.when(mockProcessBuilder.directory(Matchers.any[String]))
           .thenReturn(mockProcessBuilder)
    Mockito.when(mockProcessBuilder.start())
           .thenReturn(mockProcess)

    val mockProcessBuilderFactory = mock[NativeProcessBuilderFactory]
    Mockito.when(mockProcessBuilderFactory.apply(Matchers.any[Seq[String]]))
           .thenReturn(mockProcessBuilder)

    val expectedCacheDirectory = "some/cache/dir"
    val gitProcessName = GenNative.genAlphaNumericString(2, 5)
    val gitDownloadRemote = new GitDownloadRemote(gitProcessName, mockProcessBuilderFactory, expectedCacheDirectory)

    describe("When downloading the remote git repository") {
      val projectDto = GenModel.net.genProjectDto()
      val expectedProjectDto = projectDto
      val expectedProject = GenModel.arg.genProject()

      Await.result(gitDownloadRemote.apply(expectedProject, expectedProjectDto), Duration(1, SECONDS))

      it("Then the git download process builder is created") {
        Mockito.verify(mockProcessBuilderFactory).apply(Seq[String](gitProcessName, "clone", expectedProjectDto.git, "source"))
      }
      it("Then the git process is moved to the expected cache directory") {
        Mockito.verify(mockProcessBuilder).directory(expectedCacheDirectory + "/" + expectedProject)
      }
      it("Then the git process is started") {
        Mockito.verify(mockProcessBuilder).start()
      }
      it("Then the git process is waited for") {
        Mockito.verify(mockProcess).waitFor()
      }
    }
  }

}
