package uk.co.morleydev.zander.client.spec

import org.scalatest.FunSpec
import scala.util.Random
import uk.co.morleydev.zander.client.Main
import uk.co.morleydev.zander.client.util.Using._
import uk.co.morleydev.zander.client.model.Configuration
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito
import uk.co.morleydev.zander.client.gen.{GenStringArguments, GenNative}
import uk.co.morleydev.zander.client.data.NativeProcessBuilderFactory
import java.io.File

class AnyTests extends FunSpec with MockitoSugar {

  describe("Given Zander when running an invalid operation") {

    val operation = GenNative.genAlphaNumericStringExcluding(1, 10, GenStringArguments.operations)

    val arguments = Array[String](operation,
                                  GenStringArguments.genProject(),
                                  GenStringArguments.genCompiler(),
                                  GenStringArguments.genBuildMode())

    val mockExit = mock[Int => Unit]
    using(new TestConfigurationFile(new Configuration("http://localhost"))) { config =>
      Main.main(arguments, config.file.getAbsolutePath, mockExit, mock[NativeProcessBuilderFactory], new File("tmp"))
    }

    it("Then the program exits with the expected code") {
      Mockito.verify(mockExit)(ResponseCodes.InvalidOperation)
    }
  }

  describe("Given Zander when running an invalid compiler") {

    val compiler = GenNative.genAlphaNumericStringExcluding(1, 10, GenStringArguments.compilers)

    val arguments = Array[String](GenStringArguments.genOperation(),
      GenStringArguments.genProject(), compiler,
      GenStringArguments.genBuildMode())

    val mockExit = mock[Int => Unit]
    using(new TestConfigurationFile(new Configuration("http://localhost"))) { config =>
      Main.main(arguments, config.file.getAbsolutePath, mockExit, mock[NativeProcessBuilderFactory], new File("tmp"))
    }

    it("Then the program exits with the expected code") {
      Mockito.verify(mockExit)(ResponseCodes.InvalidCompiler)
    }
  }

  describe("Given Zander when running an invalid build mode") {

    val buildMode = GenNative.genAlphaNumericStringExcluding(1, 10, GenStringArguments.buildModes)

    val arguments = Array[String](GenStringArguments.genOperation(),
      GenStringArguments.genProject(),
      GenStringArguments.genCompiler(),
      buildMode)

    val mockExit = mock[Int => Unit]
    using(new TestConfigurationFile(new Configuration("http://localhost"))) { config =>
      Main.main(arguments, config.file.getAbsolutePath, mockExit, mock[NativeProcessBuilderFactory], new File("tmp"))
    }

    it("Then the program exits with the expected code") {
      Mockito.verify(mockExit)(ResponseCodes.InvalidBuildMode)
    }
  }

  describe("Given Zander when running any operation with a non-alphanumeric project") {

    val random = new Random
    val project = Iterator.continually(random.nextString(random.nextInt(20)+1))
                          .find(f => !f.forall(c => c.isLetterOrDigit))
                          .get

    val arguments = Array[String](GenStringArguments.genOperation(),
                                  project,
                                  GenStringArguments.genCompiler(),
                                  GenStringArguments.genBuildMode())

    val mockExit = mock[Int => Unit]
    using(new TestConfigurationFile(new Configuration(GenNative.genHttpUrl().toString))) { config =>
      Main.main(arguments, config.file.getAbsolutePath, mockExit, mock[NativeProcessBuilderFactory], new File("tmp"))
    }

    it("Then the program exits with the expected code") {
      Mockito.verify(mockExit)(ResponseCodes.InvalidProject)
    }
  }

  describe("Given Zander when running any operation with a project of invalid length") {

    val project = GenNative.genAlphaNumericString(21, 50)

    val arguments = Array[String](GenStringArguments.genOperation(),
                                  project,
                                  GenStringArguments.genCompiler(),
                                  GenStringArguments.genBuildMode())

    val mockExit = mock[Int => Unit]
    using(new TestConfigurationFile(new Configuration(GenNative.genHttpUrl().toString))) { config =>
      Main.main(arguments, config.file.getAbsolutePath, mockExit, mock[NativeProcessBuilderFactory], new File("tmp"))
    }

    it("Then the program exits with the expected code") {
      Mockito.verify(mockExit)(ResponseCodes.InvalidProject)
    }
  }

}
