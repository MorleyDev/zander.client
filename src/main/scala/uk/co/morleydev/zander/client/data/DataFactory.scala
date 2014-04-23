package uk.co.morleydev.zander.client.data

import java.io.File
import uk.co.morleydev.zander.client.model.Configuration

trait DataFactory {
  def createArtefactDetailsReaderFromLocal() : ReadProjectArtefactDetails
  def createProjectArtefactDetailsDelete() : DeleteProjectArtefactDetails
  def createProjectArtefactDeleteFromLocal() : DeleteProjectArtefacts

  def createGetProjectDtoRemote(config : Configuration) : GetProjectDto

  def createGitDownloadRemote(config : Configuration) : DownloadProjectSource
  def createGitUpdate(config : Configuration) : UpdateProjectSource
  def createGetGitVersion(config : Configuration) : GetProjectSourceVersion

  def createProjectSourceDetailsReaderFromCache(cache : File): ReadProjectCacheDetails
  def createProjectSourceDetailsWriterToCache(config : Configuration) : WriteProjectSourceDetails
  def createProjectSourceListFilesInCache(config : Configuration) : ListProjectCacheFiles

  def createCMakePreBuildCachedSource(config : Configuration) : PreBuildProjectSource
  def createCMakeBuildCachedSource(config : Configuration) : BuildProjectSource
  def createCMakeInstallCachedSource(config : Configuration) : InstallProjectCache

  def createProjectArtefactInstallFromCache(config : Configuration) : InstallProjectArtefact
  def createProjectArtefactVersionWriterToLocal() : WriteProjectArtefactDetails

  def createCheckArtefactDetailsExist() : CheckArtefactDetailsExist
}


