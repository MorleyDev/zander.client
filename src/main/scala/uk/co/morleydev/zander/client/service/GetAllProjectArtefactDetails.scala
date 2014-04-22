package uk.co.morleydev.zander.client.service

import uk.co.morleydev.zander.client.model.arg.BuildCompiler._
import uk.co.morleydev.zander.client.model.arg.BuildMode._
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.store.ArtefactDetails

trait GetAllProjectArtefactDetails extends (() => Map[(Project, BuildCompiler, BuildMode), ArtefactDetails])
