package uk.co.morleydev.zander.client.data

import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler

trait CompilerGeneratorMap extends (BuildCompiler => Seq[String])
