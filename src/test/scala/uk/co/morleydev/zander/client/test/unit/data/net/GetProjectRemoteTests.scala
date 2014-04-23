package uk.co.morleydev.zander.client.test.unit.data.net

import com.stackmob.newman.request.HttpRequest
import com.stackmob.newman.response.{HttpResponseCode, HttpResponse}
import java.net.URL
import org.mockito.{ArgumentMatcher, Mockito, Matchers}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, SECONDS}
import scala.concurrent.{Await, future}
import uk.co.morleydev.zander.client.data.exception.ProjectEndpointNotFoundException
import uk.co.morleydev.zander.client.data.net.GetProjectDtoRemote
import uk.co.morleydev.zander.client.model.arg.BuildCompiler
import uk.co.morleydev.zander.client.model.net.ProjectDto
import uk.co.morleydev.zander.client.test.gen.{GenStringArguments, GenModel, GenNative}
import uk.co.morleydev.zander.client.test.unit.UnitTest

class GetProjectRemoteTests extends UnitTest {

  describe("Given a GetProjectRemote function object") {

    describe("When getting a project for a compiler succeeds") {
      val host = "http://www." + GenNative.genAlphaNumericString(1, 50) + ".com:" + GenNative.genInt(1000, 60000)
      val mockUrlGet: (URL => HttpRequest) = mock[(URL => HttpRequest)]
      val getProjectRemote = new GetProjectDtoRemote(new URL(host), mockUrlGet)

      val mockHttpRequest = mock[HttpRequest]
      val mockHttpResponse = mock[HttpResponse]
      val expectedProject = new ProjectDto("http://git_string.com/git/string")

      Mockito.when(mockUrlGet(Matchers.any[URL]()))
        .thenReturn(mockHttpRequest)
      Mockito.when(mockHttpRequest.apply)
        .thenReturn(future(mockHttpResponse))
      Mockito.when(mockHttpResponse.code)
        .thenReturn(HttpResponseCode.Ok)
      Mockito.when(mockHttpResponse.bodyString)
        .thenReturn("{ \"git\": \"" + expectedProject.git + "\" }")

      val project = GenModel.arg.genProject()
      val compilerString = GenStringArguments.genCompiler()
      val compiler = BuildCompiler.withName(compilerString)
      val expectedPath = host + "/" + project + "/" + compilerString

      val result = getProjectRemote.apply(project, compiler)

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
      val getProjectRemote = new GetProjectDtoRemote(new URL(host), mockUrlGet)

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
        Await.result(getProjectRemote.apply(GenModel.arg.genProject(), GenModel.arg.genCompiler()), Duration.create(1, SECONDS))
      } catch {
        case e: ProjectEndpointNotFoundException => thrownException = e
      }
      it("Then the expected exception is thrown") {
        assert(thrownException != null)
      }
    }

    describe("When getting a project for a compiler causes the future to throw an exception") {

      val host = "http://www." + GenNative.genAlphaNumericString(1, 50) + ".com:" + GenNative.genInt(1000, 60000)
      val mockUrlGet: (URL => HttpRequest) = mock[(URL => HttpRequest)]
      val getProjectRemote = new GetProjectDtoRemote(new URL(host), mockUrlGet)

      val mockHttpRequest = mock[HttpRequest]
      Mockito.when(mockUrlGet(Matchers.any[URL]()))
        .thenReturn(mockHttpRequest)
      Mockito.when(mockHttpRequest.apply)
        .thenReturn(future(throw new RuntimeException))

      var thrownException: Exception = null
      try {
        Await.result(getProjectRemote.apply(GenModel.arg.genProject(), GenModel.arg.genCompiler()), Duration.create(1, SECONDS))
      } catch {
        case e: ProjectEndpointNotFoundException => thrownException = e
      }
      it("Then the expected exception is thrown") {
        assert(thrownException != null)
      }
    }
  }
}
