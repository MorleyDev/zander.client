package uk.co.morleydev.zander.client.test.unit.data.program

import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.data.program.{ProgramRunner, GitDownloadSourceToCache}
import org.scalatest.mock.MockitoSugar
import uk.co.morleydev.zander.client.test.gen.{GenNative, GenModel}
import org.mockito.Mockito
import java.io.File

class GitDownloadRemoteTests extends FunSpec with MockitoSugar {

  describe("Given a GitDownloadRemote") {

    val expectedCacheDirectory = new File("./some/cache/dir")
    val gitProcessName = GenNative.genAlphaNumericString(2, 5)
    val mockProgramRunner = mock[ProgramRunner]
    val gitDownloadRemote = new GitDownloadSourceToCache(gitProcessName, mockProgramRunner, expectedCacheDirectory)

    describe("When downloading the remote git repository") {
      val projectDto = GenModel.net.genGitSupportingProjectDto()
      val expectedProjectDto = projectDto
      val expectedProject = GenModel.arg.genProject()

      gitDownloadRemote.apply(expectedProject, expectedProjectDto)

      it("Then the process is ran") {
        Mockito.verify(mockProgramRunner).apply(
          Seq[String](gitProcessName, "clone", expectedProjectDto.git, "source"),
          new File(expectedCacheDirectory, expectedProject.value))
      }
    }
  }

}
