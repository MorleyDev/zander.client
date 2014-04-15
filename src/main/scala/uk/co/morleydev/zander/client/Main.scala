package uk.co.morleydev.zander.client

import uk.co.morleydev.zander.client.model._
import java.net.URL
import scala.io.Source
import com.lambdaworks.jacks.JacksMapper
import uk.co.morleydev.zander.client.model.arg.{Operation, Compiler, BuildMode}
import uk.co.morleydev.zander.client.validator.ProjectValidator

object Main {

  private val program = new Program(ProjectValidator)

  def main(args : Array[String], configFile : String, exit : Int => Unit) {

    val operation = try { Operation.withName(args(0)) } catch { case e : NoSuchElementException => exit(1); return }
    val project = args(1)
    val compiler = try { Compiler.withName(args(2)) } catch { case e : NoSuchElementException => exit(3); return }
    val buildMode = try { BuildMode.withName(args(3)) } catch { case e : NoSuchElementException => exit(4); return }

    val arguments = new Arguments(operation, project, compiler, buildMode)


    val configJson = Source.fromFile(configFile).getLines.mkString
    val config = JacksMapper.readValue[Configuration](configJson)

    val returnCode = program.run(arguments, config)
    exit(returnCode)
  }

  def main(args : Array[String]) {
    main(args, "config.json", System.exit)
  }
}
