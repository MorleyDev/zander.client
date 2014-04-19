package uk.co.morleydev.zander.client.service

import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.net.ProjectDto

trait ProjectSourceAcquire extends ((Project, ProjectDto) => Unit)
