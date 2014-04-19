package uk.co.morleydev.zander.client.service.impl

import uk.co.morleydev.zander.client.service.ProjectSourceAcquire
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.net.ProjectDto
import java.io.File
import uk.co.morleydev.zander.client.data.{ProjectSourceUpdate, ProjectSourceDownload}

class CachedSourceAcquire(cache : File,
                          directoryExists : (File => Boolean),
                          sourceDownload : ProjectSourceDownload,
                          sourceUpdate : ProjectSourceUpdate) extends ProjectSourceAcquire {
  override def apply(project: Project, dto: ProjectDto): Unit = {
    if ( directoryExists(new File(cache, project.value + "/source")) )
      sourceUpdate(project, dto)
    else
      sourceDownload(project, dto)
  }
}
