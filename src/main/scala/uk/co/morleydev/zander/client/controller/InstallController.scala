package uk.co.morleydev.zander.client.controller

import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import scala.concurrent.{Future, Await, future, ExecutionContext}
import scala.concurrent.duration.{Duration, MINUTES}
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.data._
import uk.co.morleydev.zander.client.service.{DownloadAcquireInstallProjectArtefacts, AcquireProjectArtefacts, CompileProjectSource, AcquireProjectSource}
import java.io.FileNotFoundException
import uk.co.morleydev.zander.client.validator.exception.LocalArtefactsAlreadyExistException
import uk.co.morleydev.zander.client.validator.ValidateArtefactDetailsExistence

class InstallController(validateArtefactsDetailsExist : ValidateArtefactDetailsExistence,
                        artefactAcquire : DownloadAcquireInstallProjectArtefacts,
                        implicit val executionContext : ExecutionContext = ExecutionContext.Implicits.global)
  extends Controller {

  override def apply(project: Project, compiler: BuildCompiler, buildMode: BuildMode): Unit = {

    validateArtefactsDetailsExist(project, compiler, buildMode)
    artefactAcquire(project, compiler, buildMode)
  }
}
