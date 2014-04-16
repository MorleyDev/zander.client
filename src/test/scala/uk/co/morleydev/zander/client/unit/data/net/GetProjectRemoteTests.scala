package uk.co.morleydev.zander.client.unit.data.net

import org.scalatest.FunSpec
import org.scalatest.mock.MockitoSugar
import java.net.URL
import uk.co.morleydev.zander.client.data.net.GetProjectRemote
import uk.co.morleydev.zander.client.model.arg.Compiler
import org.mockito.{ArgumentMatcher, Mockito, Matchers}
import scala.concurrent.{Await, future}
import com.stackmob.newman.response.{HttpResponseCode, HttpResponse}
import uk.co.morleydev.zander.client.model.net.Project
import scala.concurrent.duration.{Duration, SECONDS}
import scala.concurrent.ExecutionContext.Implicits.global
import com.stackmob.newman.request.HttpRequest
import uk.co.morleydev.zander.client.check.GenNative
import uk.co.morleydev.zander.client.data.net.exceptions.ProjectNotFoundException

class GetProjectRemoteTests extends FunSpec with MockitoSugar {

  describe("Given a GetProjectRemote function object") {

    describe("When getting a project for a compiler succeeds") {
      val host = "http://www." + GenNative.genAlphaNumericString(1, 50) + ".com:" + GenNative.genInt(1000, 60000)
      val mockUrlGet: (URL => HttpRequest) = mock[(URL => HttpRequest)]
      val getProjectRemote = new GetProjectRemote(new URL(host), mockUrlGet)

      val mockHttpRequest = mock[HttpRequest]
      val mockHttpResponse = mock[HttpResponse]
      val expectedProject = new Project("http://git_string.com/git/string")

      Mockito.when(mockUrlGet(Matchers.any[URL]()))
        .thenReturn(mockHttpRequest)
      Mockito.when(mockHttpRequest.apply)
        .thenReturn(future(mockHttpResponse))
      Mockito.when(mockHttpResponse.code)
        .thenReturn(HttpResponseCode.Ok)
      Mockito.when(mockHttpResponse.bodyString)
        .thenReturn("{ \"git\": \"" + expectedProject.git + "\" }")

      val expectedPath = host + "/some_project/msvc10"

      val result = getProjectRemote.apply("some_project", Compiler.VisualStudio10)

      it("Then the expected url is used to retrieve the data") {
        Mockito.verify(mockUrlGet).apply(Matchers.argThat(new ArgumentMatcher[URL] {
          override def matches(url: Any): Boolean = {
            url.equals(new URL(expectedPath))
          }
        }))
      }
      it("Then the returned future gives the expected project when invoked") {
        val actualProject = Await.result(result, Duration.create(1, SECONDS))
        assert(actualProject.git.equals(expectedProject.git))
      }
    }

    describe("When getting a project for a compiler gives a non-OK") {

      val host = "http://www." + GenNative.genAlphaNumericString(1, 50) + ".com:" + GenNative.genInt(1000, 60000)
      val mockUrlGet: (URL => HttpRequest) = mock[(URL => HttpRequest)]
      val getProjectRemote = new GetProjectRemote(new URL(host), mockUrlGet)

      val mockHttpRequest = mock[HttpRequest]
      val mockHttpResponse = mock[HttpResponse]

      Mockito.when(mockUrlGet(Matchers.any[URL]()))
        .thenReturn(mockHttpRequest)
      Mockito.when(mockHttpRequest.apply)
        .thenReturn(future(mockHttpResponse))
      Mockito.when(mockHttpResponse.code)
        .thenReturn(HttpResponseCode.NotFound)

      var thrownException: Exception = null
      try {
        Await.result(getProjectRemote.apply("some_project", Compiler.VisualStudio10), Duration.create(1, SECONDS))
      } catch {
        case e: ProjectNotFoundException => thrownException = e
      }
      it("Then the expected exception is thrown") {
        assert(thrownException != null)
      }
    }

    describe("When getting a project for a compiler causes the future to throw an exception") {

      val host = "http://www." + GenNative.genAlphaNumericString(1, 50) + ".com:" + GenNative.genInt(1000, 60000)
      val mockUrlGet: (URL => HttpRequest) = mock[(URL => HttpRequest)]
      val getProjectRemote = new GetProjectRemote(new URL(host), mockUrlGet)

      val mockHttpRequest = mock[HttpRequest]
      Mockito.when(mockUrlGet(Matchers.any[URL]()))
        .thenReturn(mockHttpRequest)
      Mockito.when(mockHttpRequest.apply)
        .thenReturn(future(throw new RuntimeException))

      var thrownException: Exception = null
      try {
        Await.result(getProjectRemote.apply("some_project", Compiler.VisualStudio10), Duration.create(1, SECONDS))
      } catch {
        case e: ProjectNotFoundException => thrownException = e
      }
      it("Then the expected exception is thrown") {
        assert(thrownException != null)
      }
    }
  }
}
