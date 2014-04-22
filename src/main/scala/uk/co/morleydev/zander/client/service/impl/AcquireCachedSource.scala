package uk.co.morleydev.zander.client.service.impl

import uk.co.morleydev.zander.client.service.AcquireProjectSource
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.net.ProjectDto
import java.io.File
import uk.co.morleydev.zander.client.data.{GetProjectSourceVersion, UpdateProjectSource, DownloadProjectSource}
import uk.co.morleydev.zander.client.model.store.SourceVersion

class AcquireCachedSource(cache : File,
                          directoryExists : (File => Boolean),
                          sourceDownload : DownloadProjectSource,
                          sourceUpdate : UpdateProjectSource,
                          getSourceVersion : GetProjectSourceVersion) extends AcquireProjectSource {
  override def apply(project: Project, dto: ProjectDto): SourceVersion = {
    if ( directoryExists(new File(cache, project.value + "/source")) )
      sourceUpdate(project, dto)
    else
      sourceDownload(project, dto)
    getSourceVersion(project)
  }
}
