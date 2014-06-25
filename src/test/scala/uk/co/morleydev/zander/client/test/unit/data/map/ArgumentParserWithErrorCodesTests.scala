package uk.co.morleydev.zander.client.test.unit.data.map

import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.data.ArgumentParser
import uk.co.morleydev.zander.client.data.map.ArgumentParserWithErrorCodesImpl
import uk.co.morleydev.zander.client.data.exception._
import uk.co.morleydev.zander.client.model.ExitCodes
import uk.co.morleydev.zander.client.test.gen.GenModel
import uk.co.morleydev.zander.client.test.unit.UnitTest

class ArgumentParserWithErrorCodesTests extends UnitTest {

  describe("Given an ArgumentParser") {
    val mockArgumentParser = mock[ArgumentParser]
    val argumentParserWithErrorCodes = new ArgumentParserWithErrorCodesImpl(mockArgumentParser)

    describe("When parsing valid arguments") {

      val arguments = GenModel.arg.genArguments()
      Mockito
        .when(mockArgumentParser.apply(Matchers.any[IndexedSeq[String]]))
        .thenReturn(arguments)

      val argumentArray = IndexedSeq[String]("some", "array", "of", "arguments")
      val result = argumentParserWithErrorCodes.apply(argumentArray)

      it("Then the arguments are parsed") {
        Mockito.verify(mockArgumentParser).apply(argumentArray)
      }
      it("Then the expected response is returned") {
        assert(result == Right(arguments))
      }
    }
  }

  def testCase[E <: Exception](exception : E, exitCode : Int) {
    describe("Given an ArgumentParser") {
      val mockArgumentParser = mock[ArgumentParser]
      val argumentParserWithErrorCodes = new ArgumentParserWithErrorCodesImpl(mockArgumentParser)

      describe("When parsing invalid arguments that result in an " + exception.getClass.getSimpleName) {

        Mockito
          .when(mockArgumentParser.apply(Matchers.any[IndexedSeq[String]]))
          .thenAnswer(new Answer[IndexedSeq[String]] {
          override def answer(invocation: InvocationOnMock): IndexedSeq[String] = {
            throw exception
          }
        })

        val result = argumentParserWithErrorCodes.apply(IndexedSeq[String]("some", "array", "of", "arguments"))

        it("Then the expected response is returned") {
          assert(result == Left(exitCode))
        }
      }
    }
  }
  testCase(new MissingArgumentsException, ExitCodes.InvalidArgumentCount)
  testCase(new InvalidOperationException(""), ExitCodes.InvalidOperation)
  testCase(new InvalidProjectException(""), ExitCodes.InvalidProject)
  testCase(new InvalidCompilerException(""), ExitCodes.InvalidCompiler)
  testCase(new InvalidBuildModeException(""), ExitCodes.InvalidBuildMode)
}
