package uk.co.morleydev.zander.client.data.impl

import java.io.{PrintWriter, File}
import java.net.URL
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.SystemUtils
import scala.collection.JavaConversions
import scala.io.Source
import uk.co.morleydev.zander.client.data._
import uk.co.morleydev.zander.client.data.fs._
import uk.co.morleydev.zander.client.data.map.{CMakeBuildModeBuildTypeMap, CMakeCompilerGeneratorMap}
import uk.co.morleydev.zander.client.data.net.GetProjectDtoRemote
import uk.co.morleydev.zander.client.data.program._
import uk.co.morleydev.zander.client.model.Configuration
import uk.co.morleydev.zander.client.util.Log
import uk.co.morleydev.zander.client.util.Using._

class DataFactoryImpl(processBuilderFactory : NativeProcessBuilderFactory,
                      temporaryDirectory : File,
                      workingDirectory : File) extends DataFactory {


  private def createProgramRunner() : ProgramRunner = {
    new LocalProgramRunner(processBuilderFactory)
  }

  private def writeDataToFile(data : String, file : File) : Unit = {
    using(new PrintWriter(file)) { print =>
      print.write(data)
    }
  }

  private def createCacheDirectory(config: Configuration): File = {
    new File(config.cache)
  }

  override def createGitDownloadRemote(config : Configuration) : DownloadProjectSource =
    new GitDownloadSourceToCache(config.programs.git,
      createProgramRunner(),
      createCacheDirectory(config))

  override def createGitUpdate(config : Configuration) : UpdateProjectSource =
    new GitUpdateCachedSource(config.programs.git,
      createCacheDirectory(config),
      createProgramRunner())

  override def createGetGitVersion(config : Configuration) : GetProjectSourceVersion =
    new GetGitSourceVersion(config.programs.git,
      createCacheDirectory(config),
      processBuilderFactory)

  override def createCMakePreBuildCachedSource(config : Configuration) : PreBuildProjectSource =
    new CMakePreBuildCachedSource(config.programs.cmake,
      createProgramRunner(),
      createCacheDirectory(config),
      temporaryDirectory,
      new CMakeCompilerGeneratorMap(isWindows = SystemUtils.IS_OS_WINDOWS),
      CMakeBuildModeBuildTypeMap)

  override def createCMakeBuildCachedSource(config : Configuration) =
    new CMakeBuildCachedSource(config.programs.cmake,
      createProgramRunner(),
      temporaryDirectory,
      CMakeBuildModeBuildTypeMap)

  override def createCMakeInstallCachedSource(config : Configuration) =
    new CMakeInstallCachedSource(config.programs.cmake,
      createProgramRunner(),
      temporaryDirectory,
      CMakeBuildModeBuildTypeMap)

  override def createProjectSourceDetailsReaderFromCache(cache : File): ReadProjectCacheDetails = {
    new ReadProjectCacheDetailsFromCache(cache,
      file => using(Source.fromFile(file)) {
        source => source.getLines().mkString("\n")
      })
  }

  override def createProjectSourceDetailsWriterToCache(config : Configuration) : WriteProjectSourceDetails =
    new WriteProjectSourceDetailsToCache(createCacheDirectory(config), writeDataToFile)

  override def createArtefactDetailsReaderFromLocal() : ReadProjectArtefactDetails =
    new ReadProjectArtefactDetailsFromLocal(workingDirectory, f => using(Source.fromFile(f)) {
      s => s.getLines().mkString("\n")
    })

  override def createGetProjectDtoRemote(config : Configuration) : GetProjectDto =
    new GetProjectDtoRemote(new URL(config.server))

  private def cleanEmptyDirectory(f : File) {
    if (f.exists() && FileUtils.listFiles(f, null, true).size == 0)
      FileUtils.deleteDirectory(f)
  }

  private def cleanDeleteFile(f : File) {
    f.delete()
    cleanEmptyDirectory(f.getParentFile)
  }

  override def createProjectArtefactDeleteFromLocal() : DeleteProjectArtefacts =
    new DeleteProjectArtefactsFromLocal(workingDirectory,
      cleanDeleteFile,
      cleanEmptyDirectory)

  override def createProjectArtefactInstallFromCache(config : Configuration) : InstallProjectArtefact =
    new InstallProjectArtefactFromCache(createCacheDirectory(config),
      workingDirectory.getAbsoluteFile,
      (src, dst) => {
        if (src.exists()) {
          Log.message("%s copied to %s".format(src, dst))
          if (!dst.exists())
            dst.mkdirs()

          FileUtils.copyDirectory(src, dst)
        }
        else
          Log.warning("%s copy to %s failed".format(src, dst))
      })

  override def createProjectSourceListFilesInCache(config : Configuration) : ListProjectCacheFiles =
    new ListProjectCacheFilesInCache(createCacheDirectory(config), file =>
      if (file.exists())
        JavaConversions.iterableAsScalaIterable(FileUtils.listFiles(file, null, true))
          .asInstanceOf[Iterable[File]]
          .toSeq
      else
        Seq[File]()
    )

  override def createProjectArtefactVersionWriterToLocal() : WriteProjectArtefactDetails =
    new WriteProjectArtefactDetailsToLocal(workingDirectory, writeDataToFile)

  override def createProjectArtefactDetailsDelete(): DeleteProjectArtefactDetails =
    new DeleteProjectArtefactDetailsFromLocal(workingDirectory, f => if (!f.delete()) Log.error("Could not delete file " + f.getPath))

  override def createCheckArtefactDetailsExist(): CheckArtefactDetailsExist =
    new CheckArtefactDetailsExistInLocal(workingDirectory, createArtefactDetailsReaderFromLocal())
}
