package uk.co.morleydev.zander.client.data

import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.net.ProjectDto
import scala.concurrent.Future

trait GitDownload extends ((Project, ProjectDto) => Unit)