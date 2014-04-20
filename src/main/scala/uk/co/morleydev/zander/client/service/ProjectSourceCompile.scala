package uk.co.morleydev.zander.client.service

import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.Compiler.Compiler

trait ProjectSourceCompile extends ((Project, Compiler, BuildMode) => Unit)