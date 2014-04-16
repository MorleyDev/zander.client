package uk.co.morleydev.zander.client.spec

import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.Main
import uk.co.morleydev.zander.client.util.Using.using
import com.github.kristofa.test.http.{Method, SimpleHttpResponseProvider}
import uk.co.morleydev.zander.client.model.Configuration
import uk.co.morleydev.zander.client.check.GenStringArguments
import uk.co.morleydev.zander.client.util.CreateMockHttpServer

class InstallTests extends FunSpec {

  private val arguments = Array[String]("install",
                                        GenStringArguments.genProject(),
                                        GenStringArguments.genCompiler(),
                                        GenStringArguments.genBuildMode())

  describe("Given a server does not exist") {
    describe("When an install operation is carried out with arguments " + arguments.mkString(", ")) {

      var responseCode = 0

      using(new TestConfigurationFile(new Configuration("http://localhost:24325"))) {
        config => Main.main(arguments, config.file.getAbsolutePath, s => responseCode = s)
      }
      it("Then the expected return code is returned") {
        assert(responseCode == ResponseCodes.EndpointNotFound)
      }
    }
  }

  describe("Given the project/compiler endpoint exists") {

    val endpointUrl = "/" + arguments(1) + "/" + arguments(2).toString
    val gitUrl = "http://git_url/request/at_me"
    val responseBody = "{ \"git\":\"" + gitUrl + "\" }"

    val provider = new SimpleHttpResponseProvider()
    val mockHttpServer = CreateMockHttpServer(provider)
    mockHttpServer.server.start()

    provider.expect(Method.GET, endpointUrl)
      .respondWith(200, "application/json", responseBody)

    describe("When an install operation is carried out with arguments " + arguments.mkString(", ")) {

      var responseCode = -1
      using(new TestConfigurationFile(new Configuration("http://localhost:" + mockHttpServer.port))) {
        config => Main.main(arguments, config.file.getAbsolutePath, s => responseCode = s)
      }
      it("Then the endpoint was requested") {
        provider.verify()
      }
      it("Then the expected return code is returned") {
        assert(responseCode == ResponseCodes.Success)
      }
    }
  }
}
