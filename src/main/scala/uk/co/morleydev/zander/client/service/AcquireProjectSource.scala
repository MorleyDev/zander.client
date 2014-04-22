package uk.co.morleydev.zander.client.service

import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.net.ProjectDto
import uk.co.morleydev.zander.client.model.store.SourceVersion

trait AcquireProjectSource extends ((Project, ProjectDto) => SourceVersion)
