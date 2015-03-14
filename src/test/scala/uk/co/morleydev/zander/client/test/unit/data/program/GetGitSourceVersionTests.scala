package uk.co.morleydev.zander.client.test.unit.data.program

import java.io.{ByteArrayInputStream, File}
import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.data.exception.GitVersionCheckFailedException
import uk.co.morleydev.zander.client.data.program.GetGitSourceVersion
import uk.co.morleydev.zander.client.data.{NativeProcessBuilder, NativeProcessBuilderFactory}
import uk.co.morleydev.zander.client.test.gen.{GenModel, GenNative}
import uk.co.morleydev.zander.client.test.unit.UnitTest

class GetGitSourceVersionTests extends UnitTest {

  describe("Given a git source in the cache") {

    val expectedVersion = Iterator.continually(GenNative.genAlphaNumericString(3, 20))
      .take(GenNative.genInt(1, 10))
      .mkString("\n")

    val mockProcess = mock[Process]
    Mockito.when(mockProcess.getInputStream)
      .thenReturn(new ByteArrayInputStream(expectedVersion.getBytes("UTF-8")))
    Mockito.when(mockProcess.waitFor())
      .thenReturn(0)

    val mockNativeProcessBuilder = mock[NativeProcessBuilder]
    Mockito.when(mockNativeProcessBuilder.directory(Matchers.any[File]))
      .thenReturn(mockNativeProcessBuilder)
    Mockito.when(mockNativeProcessBuilder.start())
      .thenReturn(mockProcess)

    val mockNativeProcessBuilderFactory = mock[NativeProcessBuilderFactory]
    Mockito.when(mockNativeProcessBuilderFactory.apply(Matchers.any[Seq[String]]))
      .thenReturn(mockNativeProcessBuilder)

    val git = GenNative.genAlphaNumericString(3, 10)
    val cache = new File("cache")
    val getGitSourceVersion = new GetGitSourceVersion(git,
      cache,
      mockNativeProcessBuilderFactory)

    describe("When getting the source version") {
      val project = GenModel.arg.genProject()
      val actualVersion = getGitSourceVersion(project)

      it("Then the process is created and ran") {
        Mockito.verify(mockNativeProcessBuilderFactory).apply(Seq[String](git, "rev-parse", "HEAD"))
        Mockito.verify(mockNativeProcessBuilder).directory(new File(cache, project.value + "/src"))
        Mockito.verify(mockNativeProcessBuilder).start()
        Mockito.verify(mockProcess).waitFor()
      }
      it("Then the expected version is returned") {
        assert(actualVersion.value == expectedVersion)
      }
    }
  }

  describe("Given a git source in the cache") {

    val expectedVersion = Iterator.continually(GenNative.genAlphaNumericString(3, 20))
      .take(GenNative.genInt(1, 10))
      .mkString("\n")

    val mockProcess = mock[Process]
    Mockito.when(mockProcess.getInputStream)
      .thenReturn(new ByteArrayInputStream(expectedVersion.getBytes("UTF-8")))

    val mockNativeProcessBuilder = mock[NativeProcessBuilder]
    Mockito.when(mockNativeProcessBuilder.directory(Matchers.any[File]))
      .thenReturn(mockNativeProcessBuilder)
    Mockito.when(mockNativeProcessBuilder.start())
      .thenReturn(mockProcess)

    val mockNativeProcessBuilderFactory = mock[NativeProcessBuilderFactory]
    Mockito.when(mockNativeProcessBuilderFactory.apply(Matchers.any[Seq[String]]))
      .thenReturn(mockNativeProcessBuilder)

    val git = GenNative.genAlphaNumericString(3, 10)
    val cache = new File("cache")
    val getGitSourceVersion = new GetGitSourceVersion(git,
      cache,
      mockNativeProcessBuilderFactory)

    describe("When getting the source version fails") {
      Mockito.when(mockProcess.waitFor())
        .thenReturn(GenNative.genIntExcluding(-1000, 1000, Seq[Int](0)))

      val project = GenModel.arg.genProject()
      val thrownException : Throwable = try {
        getGitSourceVersion(project)
        null
      } catch {
        case e : Throwable => e
      }

      it("Then an exception is thrown") {
        assert(thrownException != null)
      }
      it("Then the expected exception is thrown") {
        assert(thrownException.isInstanceOf[GitVersionCheckFailedException])
      }
    }
  }
}
