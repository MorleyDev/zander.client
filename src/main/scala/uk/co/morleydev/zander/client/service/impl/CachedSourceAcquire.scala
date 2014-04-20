package uk.co.morleydev.zander.client.service.impl

import uk.co.morleydev.zander.client.service.ProjectSourceAcquire
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.net.ProjectDto
import java.io.File
import uk.co.morleydev.zander.client.data.{GetProjectSourceVersion, ProjectSourceUpdate, ProjectSourceDownload}
import uk.co.morleydev.zander.client.model.store.SourceVersion

class CachedSourceAcquire(cache : File,
                          directoryExists : (File => Boolean),
                          sourceDownload : ProjectSourceDownload,
                          sourceUpdate : ProjectSourceUpdate,
                          getSourceVersion : GetProjectSourceVersion) extends ProjectSourceAcquire {
  override def apply(project: Project, dto: ProjectDto): SourceVersion = {
    if ( directoryExists(new File(cache, project.value + "/source")) )
      sourceUpdate(project, dto)
    else
      sourceDownload(project, dto)
    getSourceVersion(project)
  }
}
