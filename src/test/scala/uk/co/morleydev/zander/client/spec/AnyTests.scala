package uk.co.morleydev.zander.client.spec

import org.scalatest.FunSpec
import scala.util.Random
import uk.co.morleydev.zander.client.Main
import uk.co.morleydev.zander.client.check.GenArguments
import uk.co.morleydev.zander.client.util.Using._
import uk.co.morleydev.zander.client.model.Configuration
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito

class AnyTests extends FunSpec with MockitoSugar {

  describe("Given Zander when running an invalid operation") {

    val random = new Random
    val operation = Iterator.continually(random.alphanumeric.take(random.nextInt(10)+1).mkString)
                            .dropWhile(s => GenArguments.operations.contains(s))
                            .take(1)
                            .toList
                            .apply(0)

    val arguments = Array[String](operation,
                                  GenArguments.genProject(),
                                  GenArguments.genCompiler(),
                                  GenArguments.genBuildMode())

    val mockExit = mock[Int => Unit]
    using(new TestConfigurationFile(new Configuration("http://localhost"))) { config =>
      Main.main(arguments, config.file.getAbsolutePath, mockExit)
    }

    it("Then the program exits with the expected code") {
      Mockito.verify(mockExit)(ResponseCodes.InvalidOperation)
    }
  }

  describe("Given Zander when running an invalid compiler") {

    val random = new Random
    val compiler = Iterator.continually(random.alphanumeric.take(random.nextInt(10)+1).mkString)
      .dropWhile(s => GenArguments.compilers.contains(s))
      .take(1)
      .toList
      .apply(0)

    val arguments = Array[String](GenArguments.genOperation(),
      GenArguments.genProject(), compiler,
      GenArguments.genBuildMode())

    val mockExit = mock[Int => Unit]
    using(new TestConfigurationFile(new Configuration("http://localhost"))) { config =>
      Main.main(arguments, config.file.getAbsolutePath, mockExit)
    }

    it("Then the program exits with the expected code") {
      Mockito.verify(mockExit)(ResponseCodes.InvalidCompiler)
    }
  }

  describe("Given Zander when running an invalid build mode") {

    val random = new Random
    val buildMode = Iterator.continually(random.alphanumeric.take(random.nextInt(10)+1).mkString)
      .dropWhile(s => GenArguments.buildModes.contains(s))
      .take(1)
      .toList
      .apply(0)

    val arguments = Array[String](GenArguments.genOperation(),
      GenArguments.genProject(),
      GenArguments.genCompiler(),
      buildMode)

    val mockExit = mock[Int => Unit]
    using(new TestConfigurationFile(new Configuration("http://localhost"))) { config =>
      Main.main(arguments, config.file.getAbsolutePath, mockExit)
    }

    it("Then the program exits with the expected code") {
      Mockito.verify(mockExit)(ResponseCodes.InvalidBuildMode)
    }
  }

  describe("Given Zander when running any operation with a non-alphanumeric project") {

    val random = new Random
    val project = Iterator.continually(random.nextString(random.nextInt(20)+1))
                          .filter(f => f.count(c => !c.isLetterOrDigit) > 1)
                          .take(1)
                          .toList(0)

    val arguments = Array[String](GenArguments.genOperation(),
                                  project,
                                  GenArguments.genCompiler(),
                                  GenArguments.genBuildMode())

    val mockExit = mock[Int => Unit]
    using(new TestConfigurationFile(new Configuration("http://localhost"))) { config =>
      Main.main(arguments, config.file.getAbsolutePath, mockExit)
    }

    it("Then the program exits with the expected code") {
      Mockito.verify(mockExit)(ResponseCodes.InvalidProject)
    }
  }

  describe("Given Zander when running any operation with a project of invalid length") {

    val random = new Random
    val project = Iterator.continually(random.nextString(random.nextInt(20)+20))
      .filter(f => f.forall(c => c.isLetterOrDigit))
      .take(1)
      .toList(0)

    val arguments = Array[String](GenArguments.genOperation(),
                                  project,
                                  GenArguments.genCompiler(),
                                  GenArguments.genBuildMode())

    val mockExit = mock[Int => Unit]
    using(new TestConfigurationFile(new Configuration("http://localhost"))) { config =>
      Main.main(arguments, config.file.getAbsolutePath, mockExit)
    }

    it("Then the program exits with the expected code") {
      Mockito.verify(mockExit)(ResponseCodes.InvalidProject)
    }
  }

}
