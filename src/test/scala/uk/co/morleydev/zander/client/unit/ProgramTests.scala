package uk.co.morleydev.zander.client.unit

import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.{Configuration, Arguments, Program}
import uk.co.morleydev.zander.client.model.{Operation, Compiler, BuildMode}
import java.net.URL

class ProgramTests extends FunSpec {

  describe("Given a Program") {
    val program = new Program()

    describe("When running the program") {
      val args = new Arguments(Operation.Install, "project", Compiler.GnuCxx, BuildMode.Debug)
      val config = new Configuration(new URL("http://localhost"))
      val responseCode = program.run(args, config)

      it("Then the response code is -404") {
        assert(responseCode == -404)
      }
    }
  }
}
