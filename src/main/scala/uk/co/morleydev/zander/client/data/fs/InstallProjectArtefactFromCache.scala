package uk.co.morleydev.zander.client.data.fs

import uk.co.morleydev.zander.client.data.{GetArtefactsLocation, InstallProjectArtefact}
import uk.co.morleydev.zander.client.model.arg.{Branch, Project}
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import java.io.File

class InstallProjectArtefactFromCache(getArtefactsLocation : GetArtefactsLocation,
                                      workingDirectory : File,
                                      copyRecursive : (File, File) => Unit) extends InstallProjectArtefact {
  override def apply(project : Project, compiler : BuildCompiler, mode : BuildMode, branch : Branch) : Unit = {
    val cacheLocation = getArtefactsLocation(project, compiler, mode, branch)
    copyRecursive(new File(cacheLocation, "include"), new File(workingDirectory, "include"))
    copyRecursive(new File(cacheLocation, "lib"), new File(workingDirectory, "lib"))
    copyRecursive(new File(cacheLocation, "bin"), new File(workingDirectory, "bin"))
  }
}
