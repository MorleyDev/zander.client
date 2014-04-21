package uk.co.morleydev.zander.client.test.unit.data.program

import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.data.program.{ProgramRunner, GitUpdateCachedSource}
import uk.co.morleydev.zander.client.test.gen.{GenModel, GenNative}
import java.io.File
import org.mockito.Mockito

class GitUpdateCachedSourceTests extends FunSpec with MockitoSugar {

  describe("Given a project and dto with a git repository") {

    val git = GenNative.genAlphaNumericString(2, 10)
    val cache = new File("cache")
    val mockProgramRunner = mock[ProgramRunner]
    val gitUpdateCachedSource = new GitUpdateCachedSource(git,
                                                          cache,
                                                          mockProgramRunner)

    describe("When updating the cached source") {
      val projectDto = GenModel.net.genGitSupportingProjectDto()
      val project = GenModel.arg.genProject()
      gitUpdateCachedSource.apply(project, projectDto)

      it("Then the git update is ran") {
        Mockito.verify(mockProgramRunner).apply(Seq[String](git, "pull"), new File(cache, project.value + "/source"))
      }
    }
  }
}
