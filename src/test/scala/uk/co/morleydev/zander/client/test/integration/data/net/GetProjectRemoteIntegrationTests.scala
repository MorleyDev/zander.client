package uk.co.morleydev.zander.client.test.integration.data.net

import com.github.kristofa.test.http.{Method, SimpleHttpResponseProvider}
import java.net.URL
import org.scalatest.FunSpec
import scala.concurrent.Await
import scala.concurrent.duration._
import uk.co.morleydev.zander.client.data.exception.ProjectNotFoundException
import uk.co.morleydev.zander.client.data.net.GetProjectDtoRemote
import uk.co.morleydev.zander.client.test.gen.GenModel
import uk.co.morleydev.zander.client.test.util.CreateMockHttpServer
import uk.co.morleydev.zander.client.util.Using.using

class GetProjectRemoteIntegrationTests extends FunSpec {

  describe("Given a GetProjectRemote function object and active server") {

    val provider = new SimpleHttpResponseProvider()
    using(CreateMockHttpServer(provider)) {
      mockHttpServer =>
        mockHttpServer.server.start()

        val getProjectRemote = new GetProjectDtoRemote(new URL("http://localhost:" + mockHttpServer.port + "/"))

        describe("When requesting a project") {
          val expectedProject = GenModel.net.genGitSupportingProjectDto()
          val project = GenModel.arg.genProject()
          val compiler = GenModel.arg.genCompiler()

          provider.expect(Method.GET, "/" + project + "/" + compiler.toString)
            .respondWith(200, "application/json", "{ \"git\":\"" + expectedProject.git + "\" }")

          val projectFuture = getProjectRemote.apply(project, compiler)

          it("Then the expected project is returned") {
            val actualProject = Await.result(projectFuture, Duration(1, SECONDS))
            assert(actualProject.git.equals(expectedProject.git))
          }
        }

        describe("When requesting a project that does not exist and the future is awaited on") {
          val project = GenModel.arg.genProject()
          val compiler = GenModel.arg.genCompiler()

          provider.expect(Method.GET, "/" + project + "/" + compiler.toString)
            .respondWith(404, "application/json", "{\"code\":\"ResourceNotFound\",\"message\":\"/" + project + "/" + compiler + " does not exist\"}")

          var thrownException: Throwable = null
          val projectFuture = getProjectRemote.apply(project, compiler)
          try {
            Await.result(projectFuture, Duration(1, SECONDS))
          } catch {
            case e: Throwable => thrownException = e
          }

          it("Then an exception is thrown") {
            assert(thrownException != null)
          }
          it("Then the expected exception is thrown") {
            assert(thrownException.isInstanceOf[ProjectNotFoundException])
          }
        }
    }
  }

  describe("Given a GetProjectRemote function object and no server") {

    val getProjectRemote = new GetProjectDtoRemote(new URL("http://632a5d62-dafb-4c72-be18-11f29d890fbf.com/"))

    describe("When requesting a project/compiler the future is awaited on") {
      var thrownException : Throwable = null
      val projectFuture = getProjectRemote.apply(GenModel.arg.genProject(), GenModel.arg.genCompiler())
      try {
        Await.result(projectFuture, Duration(1, SECONDS))
      } catch {
        case e : Throwable => thrownException = e
      }
      it("Then an exception is thrown") {
        assert(thrownException != null)
      }
      it("Then the expected exception is thrown") {
        assert(thrownException.isInstanceOf[ProjectNotFoundException])
      }
    }
  }
}
