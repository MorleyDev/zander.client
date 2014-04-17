package uk.co.morleydev.zander.client.data

import uk.co.morleydev.zander.client.model.arg.Compiler.Compiler
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode

trait CMakePrebuild extends ((Project, Compiler, BuildMode) => Unit) {
}
