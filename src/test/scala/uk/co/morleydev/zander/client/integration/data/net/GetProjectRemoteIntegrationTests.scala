package uk.co.morleydev.zander.client.integration.data.net

import org.scalatest.FunSpec
import com.github.kristofa.test.http.{Method, SimpleHttpResponseProvider}
import uk.co.morleydev.zander.client.data.net.GetProjectRemote
import java.net.URL
import uk.co.morleydev.zander.client.check.GenNative
import uk.co.morleydev.zander.client.model.arg.Compiler
import scala.concurrent.Await
import scala.concurrent.duration._
import uk.co.morleydev.zander.client.model.net.Project
import uk.co.morleydev.zander.client.data.net.exceptions.ProjectNotFoundException
import uk.co.morleydev.zander.client.util.CreateMockHttpServer

class GetProjectRemoteIntegrationTests extends FunSpec {

  describe("Given a GetProjectRemote function object and active server") {

    val provider = new SimpleHttpResponseProvider()
    val mockHttpServer = CreateMockHttpServer(provider)
    mockHttpServer.server.start()

    val getProjectRemote = new GetProjectRemote(new URL("http://localhost:" + mockHttpServer.port + "/"))

    describe("When requesting a project") {
      val expectedProject = new Project("http://git/user/server")
      val projectName = GenNative.genAlphaNumericString(1, 20)
      val compiler = GenNative.genOneFrom(Compiler.values.toSeq)

      provider.expect(Method.GET, "/" + projectName + "/" + compiler.toString)
        .respondWith(200, "application/json", "{ \"git\":\"" + expectedProject.git + "\" }")

      val projectFuture = getProjectRemote.apply(projectName, compiler)

      it("Then the expected project is returned") {
        val actualProject = Await.result(projectFuture, Duration(1, SECONDS))
        assert(actualProject.git.equals(expectedProject.git))
      }
    }

    describe("When requesting a project that does not exist and the future is awaited on") {
      val projectName = GenNative.genAlphaNumericString(1, 20)
      val compiler = GenNative.genOneFrom(Compiler.values.toSeq)

      provider.expect(Method.GET, "/" + projectName + "/" + compiler.toString)
        .respondWith(404, "application/json", "{\"code\":\"ResourceNotFound\",\"message\":\"/" + projectName + "/" + compiler + " does not exist\"}")

      var thrownException : Exception = null
      val projectFuture = getProjectRemote.apply(projectName, compiler)
      try {
        Await.result(projectFuture, Duration(1, SECONDS))
      } catch {
        case e : ProjectNotFoundException => thrownException = e
        case p : Exception => println(p)
      }
      it("Then the expected exception is thrown") {
        assert(thrownException != null)
      }
    }
  }

  describe("Given a GetProjectRemote function object and no server") {

    val getProjectRemote = new GetProjectRemote(new URL("http://632a5d62-dafb-4c72-be18-11f29d890fbf.com/"))

    describe("When requesting a project/compiler the future is awaited on") {
      val projectName = GenNative.genAlphaNumericString(1, 20)
      val compiler = GenNative.genOneFrom(Compiler.values.toSeq)

      var thrownException : Exception = null
      val projectFuture = getProjectRemote.apply(projectName, compiler)
      try {
        Await.result(projectFuture, Duration(1, SECONDS))
      } catch {
        case e : ProjectNotFoundException => thrownException = e
        case p : Exception => println(p)
      }
      it("Then the expected exception is thrown") {
        assert(thrownException != null)
      }
    }
  }
}
