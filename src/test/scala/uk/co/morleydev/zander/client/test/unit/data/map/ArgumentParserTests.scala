package uk.co.morleydev.zander.client.test.unit.data.map

import uk.co.morleydev.zander.client.data.map.ArgumentParserImpl
import uk.co.morleydev.zander.client.data.exception._
import uk.co.morleydev.zander.client.model.arg._
import uk.co.morleydev.zander.client.test.gen.{GenNative, GenStringArguments}
import uk.co.morleydev.zander.client.test.unit.UnitTest

class ArgumentParserTests extends UnitTest {
  describe("Given an Argument Parser and empty set of arguments") {
    val parser = ArgumentParserImpl
    val arguments = Array[String]()

    describe("When parsing arguments") {
      var exception: Exception = null
      try parser(arguments) catch {
        case e: Exception => exception = e
      }

      it("Then the expected exception is thrown") {
        assert(exception.isInstanceOf[MissingArgumentsException])
      }
    }
  }

  describe("Given an Argument Parser and set of valid operation/project/compiler/mode arguments") {
    val parser = ArgumentParserImpl
    val arguments = GenStringArguments.genArray()

    describe("When parsing arguments") {
      val parsedArguments = parser(arguments)

      it("Then the expected parsed arguments are returned") {
        assert(parsedArguments.operation == Operation.withName(arguments(0)))
        assert(parsedArguments.operationArgs.project == new Project(arguments(1)))
        assert(parsedArguments.operationArgs.compiler == BuildCompiler.withName(arguments(2)))
        assert(parsedArguments.operationArgs.mode == BuildMode.withName(arguments(3)))
        assert(parsedArguments.operationArgs.branch == new Branch("master"))
      }
    }
  }

  describe("Given an Argument Parser and set of valid operation/project/compiler/mode and a valid branch flag") {
    val parser = ArgumentParserImpl
    val branch = GenNative.genAlphaNumericString(1, 60)
    val arguments = GenStringArguments.genArray() ++ Array("/branch:%s".format(branch))

    describe("When parsing arguments") {
      val parsedArguments = parser(arguments)

      it("Then the expected parsed arguments are returned") {
        assert(parsedArguments.operation == Operation.withName(arguments(0)))
        assert(parsedArguments.operationArgs.project == new Project(arguments(1)))
        assert(parsedArguments.operationArgs.compiler == BuildCompiler.withName(arguments(2)))
        assert(parsedArguments.operationArgs.mode == BuildMode.withName(arguments(3)))
        assert(parsedArguments.operationArgs.branch == new Branch(branch))
      }
    }
  }
  describe("Given an Argument Parser and set of valid operation/project/compiler/mode but unrecognised flags") {
    val parser = ArgumentParserImpl
    val arguments = GenStringArguments.genArray() ++ Array("--unrecognised=flag")

    describe("When parsing arguments") {
      val parsedArguments = parser(arguments)

      it("Then the expected parsed arguments are returned") {
        assert(parsedArguments.operation == Operation.withName(arguments(0)))
        assert(parsedArguments.operationArgs.project == new Project(arguments(1)))
        assert(parsedArguments.operationArgs.compiler == BuildCompiler.withName(arguments(2)))
        assert(parsedArguments.operationArgs.mode == BuildMode.withName(arguments(3)))
        assert(parsedArguments.operationArgs.branch == new Branch("master"))
      }
    }
  }

  describe("Given an Argument Parser and arguments with an invalid operation") {
    val parser = ArgumentParserImpl
    val arguments = GenStringArguments.genArray()
    arguments.update(0, GenStringArguments.genInvalidOperation())

    describe("When parsing arguments") {
      var exception: Exception = null
      try parser(arguments) catch {
        case e: Exception => exception = e
      }

      it("Then the expected exception is thrown") {
        assert(exception.isInstanceOf[InvalidOperationException])
      }
    }
  }

  describe("Given an Argument Parser and arguments with an invalid project") {
    val parser = ArgumentParserImpl
    val arguments = GenStringArguments.genArray()
    arguments.update(1, GenStringArguments.genInvalidProject())

    describe("When parsing arguments") {
      var exception: Exception = null
      try parser(arguments) catch {
        case e: Exception => exception = e
      }

      it("Then the expected exception is thrown") {
        assert(exception.isInstanceOf[InvalidProjectException])
      }
    }
  }

  describe("Given an Argument Parser and arguments with an invalid compiler") {
    val parser = ArgumentParserImpl
    val arguments = GenStringArguments.genArray()
    arguments.update(2, GenStringArguments.genInvalidCompiler())

    describe("When parsing arguments") {
      var exception: Exception = null
      try parser(arguments) catch {
        case e: Exception => exception = e
      }

      it("Then the expected exception is thrown") {
        assert(exception.isInstanceOf[InvalidCompilerException])
      }
    }
  }

  describe("Given an Argument Parser and arguments with an invalid build mode") {
    val parser = ArgumentParserImpl
    val arguments = GenStringArguments.genArray()
    arguments.update(3, GenStringArguments.genInvalidBuildMode())

    describe("When parsing arguments") {
      var exception: Exception = null
      try parser(arguments) catch {
        case e: Exception => exception = e
      }

      it("Then the expected exception is thrown") {
        assert(exception.isInstanceOf[InvalidBuildModeException])
      }
    }
  }
}
