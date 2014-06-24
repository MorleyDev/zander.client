package uk.co.morleydev.zander.client.data.map

import uk.co.morleydev.zander.client.data.ArgumentParser
import uk.co.morleydev.zander.client.exception._
import uk.co.morleydev.zander.client.model.Arguments
import uk.co.morleydev.zander.client.model.arg.{BuildCompiler, BuildMode, Operation, Project}

object ArgumentParserImpl extends ArgumentParser {
  private def tryExtractEnum(enum: Enumeration, value: String): Option[enum.Value] = {
    try {
      Some(enum.withName(value))
    } catch {
      case e: NoSuchElementException =>
        None
    }
  }
  private def tryExtractProject(value: String): Option[Project] = {
    try {
      Some(new Project(value))
    } catch {
      case e: IllegalArgumentException =>
        None
    }
  }

  def apply(args : IndexedSeq[String]): Arguments = {
    if (args.size != 4)
      throw new MissingArgumentsException()

    val operation = tryExtractEnum(Operation, args(0))
    val project = tryExtractProject(args(1))
    val compiler = tryExtractEnum(BuildCompiler, args(2))
    val buildMode = tryExtractEnum(BuildMode, args(3))

    if (operation.isEmpty)
      throw new InvalidOperationException(args(0))
    if (project.isEmpty)
      throw new InvalidProjectException(args(1))
    if (compiler.isEmpty)
      throw new InvalidCompilerException(args(2))
    if (buildMode.isEmpty)
      throw new InvalidBuildModeException(args(3))

    new Arguments(operation.get, project.get, compiler.get, buildMode.get)
  }
}
