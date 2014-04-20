package uk.co.morleydev.zander.client.service

import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.Compiler.Compiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.store.SourceVersion


trait ProjectArtefactAcquire extends ((Project, Compiler, BuildMode, SourceVersion) => Unit)
