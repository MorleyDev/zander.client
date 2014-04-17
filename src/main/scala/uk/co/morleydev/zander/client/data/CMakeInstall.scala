package uk.co.morleydev.zander.client.data


import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.Compiler.Compiler

trait CMakeInstall extends ((Project, Compiler) => Unit)

