package uk.co.morleydev.zander.client.validator.impl

import uk.co.morleydev.zander.client.validator.ValidateArtefactDetailsExistence
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.data.CheckArtefactDetailsExist
import uk.co.morleydev.zander.client.validator.exception.LocalArtefactsAlreadyExistException

class ValidateArtefactDetailsDoNotExist(check : CheckArtefactDetailsExist) extends ValidateArtefactDetailsExistence {
  override def apply(v1: Project, v2: BuildCompiler, v3: BuildMode): Unit = {
    if (check(v1,v2,v3))
      throw new LocalArtefactsAlreadyExistException
  }
}
