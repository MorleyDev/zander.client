package uk.co.morleydev.zander.client.service

import uk.co.morleydev.zander.client.model.Configuration

trait ServiceFactory {
  def createGitSourceAcquire(config : Configuration) : AcquireProjectSource
  def createCMakeProjectSourceCompile(config : Configuration) : CompileProjectSource
  def createCachedArtefactAcquire(config : Configuration) : AcquireProjectArtefacts
  def createArtefactPurgeFromLocal() : PurgeProjectArtefacts
  def createGetAllProjectArtefactDetailsFromLocal() : GetAllProjectArtefactDetails
  def createDownloadAcquireInstallProjectArtefactsFromCacheToLocal(config : Configuration) : DownloadAcquireInstallProjectArtefacts
  def createDownloadAcquireUpdateProjectArtefactsFromCacheToLocal(config : Configuration) : DownloadAcquireUpdateProjectArtefacts
}


