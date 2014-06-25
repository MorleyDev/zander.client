package uk.co.morleydev.zander.client.data.map

import uk.co.morleydev.zander.client.data.{ArgumentParser, ArgumentParserWithErrorCodes}
import uk.co.morleydev.zander.client.data.exception._
import uk.co.morleydev.zander.client.model.{ExitCodes, Arguments}
import uk.co.morleydev.zander.client.util.Log

class ArgumentParserWithErrorCodesImpl(argumentParser : ArgumentParser) extends ArgumentParserWithErrorCodes {
  def apply(args : IndexedSeq[String]) : Either[Int, Arguments] =
    try Right(argumentParser(args)) catch {
      case e: InvalidArgumentsException =>
        Log.error(e.message)
        e match {
          case e: MissingArgumentsException => Left(ExitCodes.InvalidArgumentCount)
          case e: InvalidOperationException => Left(ExitCodes.InvalidOperation)
          case e: InvalidProjectException => Left(ExitCodes.InvalidProject)
          case e: InvalidCompilerException => Left(ExitCodes.InvalidCompiler)
          case e: InvalidBuildModeException => Left(ExitCodes.InvalidBuildMode)
        }
    }
}