package uk.co.morleydev.zander.client.test.unit.data.program

import java.io.File

import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.data.exception.GitCheckoutFailedException
import uk.co.morleydev.zander.client.data.program.{GitCheckoutCachedSource, ProgramRunner}
import uk.co.morleydev.zander.client.test.gen.{GenModel, GenNative}
import uk.co.morleydev.zander.client.test.unit.UnitTest

class GitCheckoutCachedSourceTests extends UnitTest {

  describe("Given a project and dto with a git repository") {

    val git = GenNative.genAlphaNumericString(2, 10)
    val cache = new File("cache")
    val mockProgramRunner = mock[ProgramRunner]
    Mockito.when(mockProgramRunner.apply(Matchers.any[Seq[String]], Matchers.any[File]))
      .thenReturn(0)

    val gitCheckoutCachedSource = new GitCheckoutCachedSource(git,
      mockProgramRunner,
      cache)

    describe("When checking out a branch in the cached source") {
      val project = GenModel.arg.genProject()
      val branch = GenModel.arg.genBranch()
      gitCheckoutCachedSource.apply(project, branch)

      it("Then the git update is ran") {
        Mockito.verify(mockProgramRunner).apply(Seq[String](git, "checkout", branch.toString), new File(cache, project.value + "/source"))
      }
    }
  }

  describe("Given a project and dto with a git repository") {

    val git = GenNative.genAlphaNumericString(2, 10)
    val cache = new File("cache")
    val mockProgramRunner = mock[ProgramRunner]
    Mockito.when(mockProgramRunner.apply(Matchers.any[Seq[String]], Matchers.any[File]))
      .thenReturn(GenNative.genIntExcluding(-1000, 1000, Seq[Int](0)))


    val gitUpdateCachedSource = new GitCheckoutCachedSource(git,
      mockProgramRunner,
      cache)

    describe("When updating the cached source fails") {
      val project = GenModel.arg.genProject()
      val branch = GenModel.arg.genBranch()

      val thrownException : Throwable = try {
        gitUpdateCachedSource.apply(project, branch)
        null
      } catch {
        case e : Throwable => e
      }

      it("Then an exception is thrown") {
        assert(thrownException != null)
      }
      it("Then the expected exception is thrown") {
        assert(thrownException.isInstanceOf[GitCheckoutFailedException])
      }
    }
  }
}
