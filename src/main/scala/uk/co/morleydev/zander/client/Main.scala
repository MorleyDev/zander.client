package uk.co.morleydev.zander.client

import java.io.File

import uk.co.morleydev.zander.client.data.fs.LoadOrCreateConfigurationImpl
import uk.co.morleydev.zander.client.data.map.{ArgumentParserImpl, ArgumentParserWithErrorCodesImpl}
import uk.co.morleydev.zander.client.data.program.NativeProcessBuilderFactoryImpl
import uk.co.morleydev.zander.client.data.{ArgumentParserWithErrorCodes, NativeProcessBuilderFactory}
import uk.co.morleydev.zander.client.impl.ProgramFactoryImpl
import uk.co.morleydev.zander.client.model.Configuration
import uk.co.morleydev.zander.client.util._

object Main {

  def main(args : IndexedSeq[String],
           config : Configuration,
           argumentParserWithErrorCodes : ArgumentParserWithErrorCodes,
           program : Program) : Int =
    argumentParserWithErrorCodes(args)
      .fold(
        errorCode => errorCode,
        arguments => program.run(arguments, config)
      )

  def main(args : IndexedSeq[String],
           configPath : String,
           processBuilderFactory : NativeProcessBuilderFactory,
           tmp : File,
           working : File) : Int =
    main(args,
      LoadOrCreateConfigurationImpl(configPath),
      new ArgumentParserWithErrorCodesImpl(ArgumentParserImpl),
      new ProgramFactoryImpl(processBuilderFactory, tmp, working).apply())

  def main(args : Array[String]) {
    Log.enableLogging()

    val responseCode = using(new TemporaryDirectory) {
      temporaryDirectory =>
        main(args,
          new File(GetUserHomeDirectory(), "config.json").getAbsolutePath,
          NativeProcessBuilderFactoryImpl,
          temporaryDirectory, new File("").getAbsoluteFile)
    }
    System.exit(responseCode)
  }
}
