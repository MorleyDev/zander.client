package uk.co.morleydev.zander.client.service.impl

import java.io.File

import uk.co.morleydev.zander.client.data.{CheckoutProjectSource, DownloadProjectSource, GetProjectSourceVersion, UpdateProjectSource}
import uk.co.morleydev.zander.client.model.arg.{Branch, Project}
import uk.co.morleydev.zander.client.model.net.ProjectDto
import uk.co.morleydev.zander.client.model.store.SourceVersion
import uk.co.morleydev.zander.client.service.AcquireProjectSource

class AcquireCachedSource(cache : File,
                          directoryExists : (File => Boolean),
                          sourceDownload : DownloadProjectSource,
                          sourceUpdate : UpdateProjectSource,
                          sourceCheckout : CheckoutProjectSource,
                          getSourceVersion : GetProjectSourceVersion) extends AcquireProjectSource {
  override def apply(project: Project, dto: ProjectDto, branch: Branch): SourceVersion = {
    if ( directoryExists(new File(cache, project.value + "/src")) )
      sourceUpdate(project, dto)
    else
      sourceDownload(project, dto)
    sourceCheckout(project, branch)
    getSourceVersion(project)
  }
}
