package uk.co.morleydev.zander.client.util

import uk.co.morleydev.zander.client.data.NativeProcessBuilder
import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.gen.GenNative
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock
import java.io.File
import org.scalatest.mock.MockitoSugar

object CreateMockProcess extends ((() => Int) => (NativeProcessBuilder, Process)) with MockitoSugar {

  def apply(stubBehaviour: () => Int = () => 0): (NativeProcessBuilder, Process) = {

    val mockProcess = mock[Process]
    Mockito.when(mockProcess.exitValue())
      .thenReturn(0)
    Mockito.when(mockProcess.getInputStream)
      .thenReturn(GenNative.genInputStreamString())
    Mockito.when(mockProcess.getErrorStream)
      .thenReturn(GenNative.genInputStreamString())
    Mockito.when(mockProcess.waitFor())
      .thenAnswer(new Answer[Int] {
      override def answer(invocation: InvocationOnMock): Int = {
        stubBehaviour()
      }
    })

    val mockProcessBuilder = mock[NativeProcessBuilder]
    Mockito.when(mockProcessBuilder.directory(Matchers.any[File]()))
      .thenReturn(mockProcessBuilder)
    Mockito.when(mockProcessBuilder.start())
      .thenReturn(mockProcess)

    (mockProcessBuilder, mockProcess)
  }
}
