package uk.co.morleydev.zander.client.test.spec

import java.io.File
import org.scalatest.FunSpec
import org.scalatest.mock.MockitoSugar
import scala.util.Random
import uk.co.morleydev.zander.client.Main
import uk.co.morleydev.zander.client.data.NativeProcessBuilderFactory
import uk.co.morleydev.zander.client.model.Configuration
import uk.co.morleydev.zander.client.test.gen.{GenStringArguments, GenNative}
import uk.co.morleydev.zander.client.util.Using._

class AnyTests extends FunSpec with MockitoSugar {

  describe("Given Zander when running an invalid operation") {

    val operation = GenNative.genAlphaNumericStringExcluding(1, 10, GenStringArguments.operations)

    val arguments = Array[String](operation,
                                  GenStringArguments.genProject(),
                                  GenStringArguments.genCompiler(),
                                  GenStringArguments.genBuildMode())

    var responseCode = 0
    using(new TestConfigurationFile(new Configuration("http://localhost"))) { config =>
      responseCode = Main.main(arguments,
        config.file.getAbsolutePath,
        mock[NativeProcessBuilderFactory],
        new File("tmpAny0"),
        new File("working_directory_AnyTests0"))
    }

    it("Then the program exits with the expected code") {
      assert(responseCode == ResponseCodes.InvalidOperation)
    }
  }

  describe("Given Zander when running an invalid compiler") {

    val compiler = GenNative.genAlphaNumericStringExcluding(1, 10, GenStringArguments.compilers)

    val arguments = Array[String](GenStringArguments.genOperation(),
      GenStringArguments.genProject(), compiler,
      GenStringArguments.genBuildMode())

    var responseCode = 0
    using(new TestConfigurationFile(new Configuration("http://localhost"))) { config =>
      responseCode = Main.main(arguments,
        config.file.getAbsolutePath,
        mock[NativeProcessBuilderFactory],
        new File("tmpAny1"),
        new File("working_directory_AnyTests1"))
    }

    it("Then the program exits with the expected code") {
      assert(responseCode == ResponseCodes.InvalidCompiler)
    }
  }

  describe("Given Zander when running an invalid build mode") {

    val buildMode = GenNative.genAlphaNumericStringExcluding(1, 10, GenStringArguments.buildModes)

    val arguments = Array[String](GenStringArguments.genOperation(),
      GenStringArguments.genProject(),
      GenStringArguments.genCompiler(),
      buildMode)

    var responseCode = -1
    using(new TestConfigurationFile(new Configuration("http://localhost"))) { config =>
      responseCode = Main.main(arguments,
        config.file.getAbsolutePath,
        mock[NativeProcessBuilderFactory],
        new File("tmpAny2"),
        new File("working_directory_AnyTests2"))
    }

    it("Then the program exits with the expected code") {
      assert(responseCode == ResponseCodes.InvalidBuildMode)
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

    var responseCode = 0
    using(new TestConfigurationFile(new Configuration(GenNative.genHttpUrl().toString))) { config =>
      responseCode = Main.main(arguments,
        config.file.getAbsolutePath,
        mock[NativeProcessBuilderFactory],
        new File("tmpAny3"),
        new File("working_directory_AnyTests3"))
    }

    it("Then the program exits with the expected code") {
      assert(responseCode == ResponseCodes.InvalidProject)
    }
  }

  describe("Given Zander when running any operation with a project of invalid length") {

    val project = GenNative.genAlphaNumericString(21, 50)

    val arguments = Array[String](GenStringArguments.genOperation(),
                                  project,
                                  GenStringArguments.genCompiler(),
                                  GenStringArguments.genBuildMode())

    var responseCode = 0
    using(new TestConfigurationFile(new Configuration(GenNative.genHttpUrl().toString))) { config =>
      responseCode = Main.main(arguments,
        config.file.getAbsolutePath,
        mock[NativeProcessBuilderFactory],
        new File("tmpAny4"),
        new File("working_directory_AnyTests4"))
    }

    it("Then the program exits with the expected code") {
      assert(responseCode == ResponseCodes.InvalidProject)
    }
  }

}
