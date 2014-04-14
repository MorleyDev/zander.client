package uk.co.morleydev.zander.client.unit

import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.{Configuration, Arguments, Program}
import uk.co.morleydev.zander.client.model.{Operation, Compiler, BuildMode}
import java.net.URL

class ProgramTests extends FunSpec {

  describe("Given a Program") {
    val program = new Program()

    describe("When running the program") {
      program.run(new Arguments(Operation.Install, "project", Compiler.GnuCxx, BuildMode.Debug), new Configuration(new URL("http://localhost")))
    }
  }
}
