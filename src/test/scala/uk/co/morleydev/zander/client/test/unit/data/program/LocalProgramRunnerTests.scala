package uk.co.morleydev.zander.client.test.unit.data.program

import java.io.File
import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.data.program.LocalProgramRunner
import uk.co.morleydev.zander.client.data.{NativeProcessBuilderFactory, NativeProcessBuilder}
import uk.co.morleydev.zander.client.test.gen.GenNative
import uk.co.morleydev.zander.client.test.unit.UnitTest

class LocalProgramRunnerTests extends UnitTest {
  describe("Given a local program runner") {

    val mockProcess = mock[Process]
    Mockito.when(mockProcess.getInputStream)
      .thenReturn(GenNative.genInputStreamString())
    Mockito.when(mockProcess.getErrorStream)
      .thenReturn(GenNative.genInputStreamString())

    val expectedResponseCode = GenNative.genInt(-1000, 1000)
    Mockito.when(mockProcess.waitFor())
      .thenReturn(expectedResponseCode)

    val mockProcessBuilder = mock[NativeProcessBuilder]
    Mockito.when(mockProcessBuilder.directory(Matchers.any[File]))
      .thenReturn(mockProcessBuilder)
    Mockito.when(mockProcessBuilder.start())
      .thenReturn(mockProcess)

    val mockProcessBuilderFactory = mock[NativeProcessBuilderFactory]
    Mockito.when(mockProcessBuilderFactory.apply(Matchers.any[Seq[String]]))
      .thenReturn(mockProcessBuilder)

    val runner = new LocalProgramRunner(mockProcessBuilderFactory)

    describe("When running the local program") {

      val commands = Seq[String]("some", "sequence", "doing", "stuff")
      val directory = new File("../asda/sfagasg")
      val responseCode = runner.apply(commands, directory)

      it("Then the process builder is created") {
        Mockito.verify(mockProcessBuilderFactory).apply(commands)
      }
      it("Then the process is moved to the expected directory") {
        Mockito.verify(mockProcessBuilder).directory(directory)
      }
      it("Then the process is started") {
        Mockito.verify(mockProcessBuilder).start()
      }
      it("Then the process is waited for") {
        Mockito.verify(mockProcess).waitFor()
      }
      it("Then the expected response code is returned") {
        assert(responseCode == expectedResponseCode)
      }
    }
  }
}
