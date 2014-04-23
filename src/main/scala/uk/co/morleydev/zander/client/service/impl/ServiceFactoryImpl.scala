package uk.co.morleydev.zander.client.service.impl

import java.io.File
import org.apache.commons.io.FileUtils
import scala.collection.JavaConversions
import uk.co.morleydev.zander.client.data.NativeProcessBuilderFactory
import uk.co.morleydev.zander.client.data.impl.DataFactoryImpl
import uk.co.morleydev.zander.client.data.map.{SplitJsonFileNameToProjectDetails, RemoveOverlappingFilesFromArtefactDetails}
import uk.co.morleydev.zander.client.model.Configuration
import uk.co.morleydev.zander.client.service._

class ServiceFactoryImpl(processBuilderFactory : NativeProcessBuilderFactory,
                         temporaryDirectory : File,
                         workingDirectory : File) extends ServiceFactory {

  private val dataFactory = new DataFactoryImpl(processBuilderFactory, temporaryDirectory, workingDirectory)

  override def createGitSourceAcquire(config : Configuration) : AcquireProjectSource = {

    val gitDownloadRemote = dataFactory.createGitDownloadRemote(config)
    val gitUpdateRemote = dataFactory.createGitUpdate(config)
    val getGitSourceVersion = dataFactory.createGetGitVersion(config)

    new AcquireCachedSource(new File(config.cache),
                            f => f.exists() && f.isDirectory,
                            gitDownloadRemote,
                            gitUpdateRemote,
                            getGitSourceVersion)
  }

  override def createCMakeProjectSourceCompile(config : Configuration) : CompileProjectSource = {

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

  override def createCachedArtefactAcquire(config : Configuration) : AcquireProjectArtefacts = {

    val install = dataFactory.createProjectArtefactInstallFromCache(config)
    val listFilesInCache = dataFactory.createProjectSourceListFilesInCache(config)
    val write = dataFactory.createProjectArtefactVersionWriterToLocal()

    new AcquireCachedArtefacts(install, listFilesInCache, write)
  }

  override def createArtefactPurgeFromLocal() : PurgeProjectArtefacts = {
    val getDetails = createGetAllProjectArtefactDetailsFromLocal()
    val deleteDetails = dataFactory.createProjectArtefactDetailsDelete()
    val deleteArtefacts = dataFactory.createProjectArtefactDeleteFromLocal()

    new LocalArtefactPurge(getDetails, RemoveOverlappingFilesFromArtefactDetails, deleteDetails, deleteArtefacts)
  }

  private def listFilesInDirectory(directory : File, filter : String) : Seq[File] = {
    JavaConversions.collectionAsScalaIterable(FileUtils.listFiles(directory, Array[String](filter), false))
                               .asInstanceOf[Iterable[File]]
                               .toSeq
  }

  override def createGetAllProjectArtefactDetailsFromLocal() : GetAllProjectArtefactDetails =
    new GetAllProjectArtefactDetailsFromLocal(workingDirectory,
      listFilesInDirectory,
      SplitJsonFileNameToProjectDetails,
      dataFactory.createArtefactDetailsReaderFromLocal())

  override def createDownloadAcquireInstallProjectArtefactsFromCacheToLocal(config : Configuration) : DownloadAcquireInstallProjectArtefacts = {

    val getDtoRemote = dataFactory.createGetProjectDtoRemote(config)
    val sourceAcquireService = createGitSourceAcquire(config)
    val sourceCompileService = createCMakeProjectSourceCompile(config)
    val artefactAcquire = createCachedArtefactAcquire(config)

    new DownloadAcquireInstallProjectArtefactsFromCacheToLocal(getDtoRemote, sourceAcquireService, sourceCompileService, artefactAcquire)
  }

  override def createDownloadAcquireUpdateProjectArtefactsFromCacheToLocal(config : Configuration) : DownloadAcquireUpdateProjectArtefacts = {
    val purge= createArtefactPurgeFromLocal()
    val downloadAcquireInstall = createDownloadAcquireInstallProjectArtefactsFromCacheToLocal(config)

    new DownloadAcquireUpdateProjectArtefactsFromCacheToLocal(purge,downloadAcquireInstall)
  }
}
