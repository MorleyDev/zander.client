package uk.co.morleydev.zander.client.test.unit.data.program

import java.io.File
import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.data.exception.GitUpdateFailedException
import uk.co.morleydev.zander.client.data.program.{ProgramRunner, GitUpdateCachedSource}
import uk.co.morleydev.zander.client.test.gen.{GenModel, GenNative}
import uk.co.morleydev.zander.client.test.unit.UnitTest

class GitUpdateCachedSourceTests extends UnitTest {

  describe("Given a project and dto with a git repository") {

    val git = GenNative.genAlphaNumericString(2, 10)
    val cache = new File("cache")
    val mockProgramRunner = mock[ProgramRunner]
    Mockito.when(mockProgramRunner.apply(Matchers.any[Seq[String]], Matchers.any[File]))
      .thenReturn(0)

    val gitUpdateCachedSource = new GitUpdateCachedSource(git,
                                                          cache,
                                                          mockProgramRunner)

    describe("When updating the cached source") {
      val projectDto = GenModel.net.genGitSupportingProjectDto()
      val project = GenModel.arg.genProject()
      gitUpdateCachedSource.apply(project, projectDto)

      it("Then the git checkout of master is ran") {
        Mockito.verify(mockProgramRunner).apply(Seq[String](git, "checkout", "master"), new File(cache, project.value + "/src"))
      }
      it("Then the git update is ran") {
        Mockito.verify(mockProgramRunner).apply(Seq[String](git, "pull"), new File(cache, project.value + "/src"))
      }
    }
  }

  describe("Given a project and dto with a git repository") {

    val git = GenNative.genAlphaNumericString(2, 10)
    val cache = new File("cache")
    val mockProgramRunner = mock[ProgramRunner]
    Mockito.when(mockProgramRunner.apply(Matchers.any[Seq[String]], Matchers.any[File]))
      .thenReturn(GenNative.genIntExcluding(-1000, 1000, Seq[Int](0)))

    val gitUpdateCachedSource = new GitUpdateCachedSource(git,
      cache,
      mockProgramRunner)

    describe("When updating the cached source fails") {
      val projectDto = GenModel.net.genGitSupportingProjectDto()
      val project = GenModel.arg.genProject()

      val thrownException : Throwable = try {
        gitUpdateCachedSource.apply(project, projectDto)
        null
      } catch {
        case e : Throwable => e
      }

      it("Then an exception is thrown") {
        assert(thrownException != null)
      }
      it("Then the expected exception is thrown") {
        assert(thrownException.isInstanceOf[GitUpdateFailedException])
      }
    }
  }
}
