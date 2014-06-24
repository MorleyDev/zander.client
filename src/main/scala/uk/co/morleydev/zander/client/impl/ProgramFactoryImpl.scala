package uk.co.morleydev.zander.client.impl

import java.io.File

import uk.co.morleydev.zander.client.controller.impl.ControllerFactoryImpl
import uk.co.morleydev.zander.client.data.NativeProcessBuilderFactory
import uk.co.morleydev.zander.client.{Program, ProgramFactory}


class ProgramFactoryImpl(builder : NativeProcessBuilderFactory, temporaryDirectory: File, workingDirectory: File) extends ProgramFactory {
  private val controllerFactory = new ControllerFactoryImpl(builder, temporaryDirectory, workingDirectory)

  def apply(): Program =
    new Program(controllerFactory)
}
