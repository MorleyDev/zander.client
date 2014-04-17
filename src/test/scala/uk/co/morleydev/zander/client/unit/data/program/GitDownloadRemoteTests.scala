package uk.co.morleydev.zander.client.unit.data.program

import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.data.program.{ProgramRunner, GitDownloadRemote}
import org.scalatest.mock.MockitoSugar
import uk.co.morleydev.zander.client.gen.{GenNative, GenModel}
import org.mockito.Mockito
import java.io.File

class GitDownloadRemoteTests extends FunSpec with MockitoSugar {

  describe("Given a GitDownloadRemote") {

    val expectedCacheDirectory = new File("./some/cache/dir")
    val gitProcessName = GenNative.genAlphaNumericString(2, 5)
    val mockProgramRunner = mock[ProgramRunner]
    val gitDownloadRemote = new GitDownloadRemote(gitProcessName, mockProgramRunner, expectedCacheDirectory)

    describe("When downloading the remote git repository") {
      val projectDto = GenModel.net.genProjectDto()
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
