package uk.co.morleydev.zander.client.service

import java.io.{PrintWriter, File}
import org.apache.commons.io.FileUtils
import scala.collection.JavaConversions
import uk.co.morleydev.zander.client.data.fs._
import uk.co.morleydev.zander.client.data.{DataFactoryImpl, NativeProcessBuilderFactory}
import uk.co.morleydev.zander.client.model.Configuration
import uk.co.morleydev.zander.client.service.impl.{LocalArtefactPurge, AcquireCachedArtefacts, CompileCachedProjectSource, AcquireCachedSource}
import uk.co.morleydev.zander.client.util.Log
import uk.co.morleydev.zander.client.util.Using.using

class ServiceFactoryImpl(processBuilderFactory : NativeProcessBuilderFactory,
                         temporaryDirectory : File,
                         workingDirectory : File) {

  private val dataFactory = new DataFactoryImpl(processBuilderFactory, temporaryDirectory, workingDirectory)

  def createGitSourceAcquire(config : Configuration) : AcquireProjectSource = {

    val gitDownloadRemote = dataFactory.createGitDownloadRemote(config)
    val gitUpdateRemote = dataFactory.createGitUpdate(config)
    val getGitSourceVersion = dataFactory.createGetGitVersion(config)

    new AcquireCachedSource(new File(config.cache),
                            f => f.exists() && f.isDirectory,
                            gitDownloadRemote,
                            gitUpdateRemote,
                            getGitSourceVersion)
  }

  def createCMakeProjectSourceCompile(config : Configuration) : CompileProjectSource = {

    val cacheDirectory = new File(config.cache)

    val detailsReader = dataFactory.createProjectSourceDetailsReaderFromCache(cacheDirectory)
    val preBuild = dataFactory.createCMakePreBuildCachedSource(config)
    val build = dataFactory.createCMakeBuildCachedSource(config)
    val install = dataFactory.createCMakeInstallCachedSource(config)
    val detailsWriter = dataFactory.createProjectSourceDetailsWriterToCache(config)

    new CompileCachedProjectSource(detailsReader,
      (p,c,m) => FileUtils.deleteDirectory(new File(cacheDirectory, "%s/%s.%s".format(p,c,m))),
      preBuild,
      build,
      install,
      detailsWriter)
  }

  def createCachedArtefactAcquire(config : Configuration) : AcquireProjectArtefacts = {

    val install = dataFactory.createProjectArtefactInstallFromCache(config)
    val listFilesInCache = dataFactory.createProjectSourceListFilesInCache(config)
    val write = dataFactory.createProjectArtefactVersionWriterToLocal()

    new AcquireCachedArtefacts(install, listFilesInCache, write)
  }

  def createArtefactPurgeFromLocal() : PurgeProjectArtefacts = {
    val readDetails = dataFactory.createArtefactDetailsReaderFromLocal()
    val deleteDetails = dataFactory.createProjectArtefactDetailsDelete()
    val deleteArtefacts = dataFactory.createProjectArtefactDeleteFromLocal()

    new LocalArtefactPurge(readDetails, deleteDetails, deleteArtefacts)
  }
}
