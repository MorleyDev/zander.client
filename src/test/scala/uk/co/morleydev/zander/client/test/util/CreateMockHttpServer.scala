package uk.co.morleydev.zander.client.test.util

import com.github.kristofa.test.http.{MockHttpServer, AbstractHttpResponseProvider}
import uk.co.morleydev.zander.client.test.gen.GenNative
import java.net.BindException

class MockServerAndPort(val server : MockHttpServer, val port : Int) extends AutoCloseable {

  override def close(): Unit = {
    server.stop()
  }
}

object CreateMockHttpServer extends (AbstractHttpResponseProvider => MockServerAndPort) {

  override def apply(provider: AbstractHttpResponseProvider): MockServerAndPort = {
    var mockHttpServer : MockHttpServer = null
    val mockPort = Iterator.continually(GenNative.genInt(8000, 60000))
      .dropWhile(port => {
      try {
        mockHttpServer = new MockHttpServer(port, provider)
        false
      } catch {
        case e : BindException => true
      }
    }).take(1).toList.head
    new MockServerAndPort(mockHttpServer, mockPort)
  }
}
