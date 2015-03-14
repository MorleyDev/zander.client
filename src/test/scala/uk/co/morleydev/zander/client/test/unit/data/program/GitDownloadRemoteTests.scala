package uk.co.morleydev.zander.client.test.unit.data.program

import java.io.File
import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.data.exception.GitDownloadFailedException
import uk.co.morleydev.zander.client.data.program.{ProgramRunner, GitDownloadSourceToCache}
import uk.co.morleydev.zander.client.test.gen.{GenNative, GenModel}
import uk.co.morleydev.zander.client.test.unit.UnitTest

class GitDownloadRemoteTests extends UnitTest {

  describe("Given a GitDownloadRemote") {
    val expectedCacheDirectory = new File("./some/cache/dir")
    val gitProcessName = GenNative.genAlphaNumericString(2, 5)
    val mockProgramRunner = mock[ProgramRunner]
    val gitDownloadRemote = new GitDownloadSourceToCache(gitProcessName, mockProgramRunner, expectedCacheDirectory)

    describe("When downloading the remote git repository") {
      Mockito.when(mockProgramRunner.apply(Matchers.any[Seq[String]], Matchers.any[File]))
      .thenReturn(0)

      val projectDto = GenModel.net.genGitSupportingProjectDto()
      val expectedProjectDto = projectDto
      val expectedProject = GenModel.arg.genProject()

      gitDownloadRemote.apply(expectedProject, expectedProjectDto)

      it("Then the process is ran") {
        Mockito.verify(mockProgramRunner).apply(
          Seq[String](gitProcessName, "clone", expectedProjectDto.src.href, "src"),
          new File(expectedCacheDirectory, expectedProject.value))
      }
    }
  }

  describe("Given a GitDownloadRemote") {
    val expectedCacheDirectory = new File("./some/cache/dir")
    val gitProcessName = GenNative.genAlphaNumericString(2, 5)
    val mockProgramRunner = mock[ProgramRunner]
    val gitDownloadRemote = new GitDownloadSourceToCache(gitProcessName, mockProgramRunner, expectedCacheDirectory)

    describe("When downloading the remote git repository fails") {

      Mockito.when(mockProgramRunner.apply(Matchers.any[Seq[String]], Matchers.any[File]))
             .thenReturn(GenNative.genIntExcluding(-1000, 1000, Seq[Int](0)))

      val projectDto = GenModel.net.genGitSupportingProjectDto()
      val expectedProjectDto = projectDto
      val expectedProject = GenModel.arg.genProject()

      val thrownException : Throwable = try {
        gitDownloadRemote.apply(expectedProject, expectedProjectDto)
        null
      } catch {
        case e : Throwable => e
      }

      it("Then an exception is thrown") {
        assert(thrownException != null)
      }
      it("Then the expected exception is thrown") {
        assert(thrownException.isInstanceOf[GitDownloadFailedException])
      }
    }
  }
}
