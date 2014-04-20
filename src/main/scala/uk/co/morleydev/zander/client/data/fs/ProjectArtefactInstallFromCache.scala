package uk.co.morleydev.zander.client.data.fs

import uk.co.morleydev.zander.client.data.ProjectArtefactInstall
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import java.io.File

class ProjectArtefactInstallFromCache(cache : File,
                                      workingDirectory : File,
                                      copyRecursive : (File, File) => Unit) extends ProjectArtefactInstall {
  override def apply(project : Project, compiler : BuildCompiler, mode : BuildMode) : Unit = {
    copyRecursive(new File(cache, "%s/%s.%s/include".format(project, compiler, mode)), new File(workingDirectory, "include"))
    copyRecursive(new File(cache, "%s/%s.%s/lib".format(project, compiler, mode)), new File(workingDirectory, "lib"))
    copyRecursive(new File(cache, "%s/%s.%s/bin".format(project, compiler, mode)), new File(workingDirectory, "bin"))
  }
}
