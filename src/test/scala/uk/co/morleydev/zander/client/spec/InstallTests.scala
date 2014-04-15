package uk.co.morleydev.zander.client.spec

import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.{Main}
import uk.co.morleydev.zander.client.check.GenArguments
import uk.co.morleydev.zander.client.util.Using.using
import com.github.kristofa.test.http.{Method, SimpleHttpResponseProvider, MockHttpServer}
import uk.co.morleydev.zander.client.model.Configuration

class InstallTests extends FunSpec {

  describe("Given a server does not exist") {
    describe("When an install operation is carried out") {

      val arguments = GenArguments.genInstall().sample.get
      var responseCode = 0

      using(new TestConfigurationFile(new Configuration("http://localhost"))) {
        config => Main.main(arguments, config.file.getAbsolutePath, s => responseCode = s)
      }
      it("Then the expected return code is returned") {
        assert(responseCode == -404)
      }
    }
  }

  describe("Given the project/compiler endpoint exists") {

    val arguments : Array[String] = GenArguments.genInstall().sample.get
    val endpointUrl = "/" + arguments(0) + "/" + arguments(2).toString
    val gitUrl = "http://git_url/request/at_me"
    val responseBody = "{ \"git\":\"" + gitUrl + "\" }"

    val provider = new SimpleHttpResponseProvider()
    val mockHttpServer = new MockHttpServer(8080, provider)
    mockHttpServer.start()

    provider.expect(Method.GET, endpointUrl)
            .respondWith(200, "application/json", responseBody)

    describe("When an install operation is carried out") {

      var responseCode = -1
      using(new TestConfigurationFile(new Configuration("http://localhost:8080"))) {
        config => Main.main(arguments, config.file.getAbsolutePath, s => responseCode = s)
      }
      it("Then the endpoint was requested") {
        provider.verify()
      }
      it("Then the expected return code is returned") {
        assert(responseCode == 0)
      }
    }
  }
}
