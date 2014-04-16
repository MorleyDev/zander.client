package uk.co.morleydev.zander.client.spec.install

import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.Main
import uk.co.morleydev.zander.client.util.Using.using
import com.github.kristofa.test.http.{Method, SimpleHttpResponseProvider}
import uk.co.morleydev.zander.client.model.Configuration
import uk.co.morleydev.zander.client.gen.GenStringArguments
import uk.co.morleydev.zander.client.util.CreateMockHttpServer
import org.scalatest.mock.MockitoSugar
import uk.co.morleydev.zander.client.spec.{ResponseCodes, TestConfigurationFile}
import uk.co.morleydev.zander.client.data.NativeProcessBuilderFactory

class InstallFailsTests extends FunSpec with MockitoSugar {

  private val arguments = Array[String]("install",
                                        GenStringArguments.genProject(),
                                        GenStringArguments.genCompiler(),
                                        GenStringArguments.genBuildMode())

  describe("Given a server does not exist") {
    describe("When an install operation is carried out with arguments " + arguments.mkString(", ")) {

      var responseCode = 0

      val configuration = new Configuration("http://localhost:24325", cache = "./cache/directory/")
      using(new TestConfigurationFile(configuration)) {
        config => Main.main(arguments, config.file.getAbsolutePath, s => responseCode = s, mock[NativeProcessBuilderFactory])
      }
      it("Then the expected return code is returned") {
        assert(responseCode == ResponseCodes.EndpointNotFound)
      }
    }
  }

  describe("Given a server but the endpoint does not exist") {

    val endpointUrl = "/" + arguments(1) + "/" + arguments(2)
    val provider = new SimpleHttpResponseProvider()
    val mockHttpServer = CreateMockHttpServer(provider)
    mockHttpServer.server.start()

    provider.expect(Method.GET, endpointUrl)
      .respondWith(404, "application/json", "{ }")

    describe("When an install operation is carried out with arguments " + arguments.mkString(", ")) {

      var responseCode = 0

      using(new TestConfigurationFile(new Configuration("http://localhost:24325"))) {
        config => Main.main(arguments, config.file.getAbsolutePath, s => responseCode = s, mock[NativeProcessBuilderFactory])
      }
      it("Then the expected return code is returned") {
        assert(responseCode == ResponseCodes.EndpointNotFound)
      }
    }
  }
}
