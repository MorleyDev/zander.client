package uk.co.morleydev.zander.client.data.program

import uk.co.morleydev.zander.client.data.{NativeProcessBuilderFactory, NativeProcessBuilder}

object NativeProcessBuilderFactoryImpl extends NativeProcessBuilderFactory {
  override def apply(commands :  Seq[String]): NativeProcessBuilder =
    new NativeProcessBuilderImpl(commands)
}
