package uk.co.morleydev.zander.client.service

import uk.co.morleydev.zander.client.data.NativeProcessBuilderFactory
import java.io.{PrintWriter, File}
import uk.co.morleydev.zander.client.data.program._
import uk.co.morleydev.zander.client.service.impl.{CachedArtefactAcquire, CachedSourceCompile, CachedSourceAcquire}
import uk.co.morleydev.zander.client.model.Configuration
import uk.co.morleydev.zander.client.data.fs.{ProjectSourceDetailsReaderFromCache, ProjectSourceDetailsWriterToCache, ProjectArtefactVersionWriterToLocal, ProjectArtefactInstallFromCache}
import uk.co.morleydev.zander.client.util.Log
import uk.co.morleydev.zander.client.util.Using.using
import org.apache.commons.io.FileUtils
import scala.io.Source

class ServiceFactory(processBuilderFactory : NativeProcessBuilderFactory,
                     temporaryDirectory : File,
                     workingDirectory : File) {

  private def writeDataToFile(data : String, file : File) : Unit = {
    using(new PrintWriter(file)) { print =>
      print.write(data)
    }
  }

 private val programRunner = new LocalProgramRunner(processBuilderFactory)

  def createGitSourceAcquire(config : Configuration) : ProjectSourceAcquire = {

    val cacheDirectory = new File(config.cache)
    val gitDownloadRemote = new GitDownloadSourceToCache(config.programs.git,
                                                         programRunner,
                                                         cacheDirectory)
    val gitUpdateRemote = new GitUpdateCachedSource(config.programs.git,
                                                    cacheDirectory,
                                                    programRunner)
    val getGitSourceVersion = new GetGitSourceVersion(config.programs.git,
                                                      cacheDirectory,
                                                      processBuilderFactory)

    new CachedSourceAcquire(cacheDirectory,
                            f => f.exists() && f.isDirectory,
                            gitDownloadRemote,
                            gitUpdateRemote,
                            getGitSourceVersion)
  }

  def createCMakeProjectSourceCompile(config : Configuration) : ProjectSourceCompile = {

    val cacheDirectory = new File(config.cache)

    val detailsReader = new ProjectSourceDetailsReaderFromCache(cacheDirectory,
      file => using(Source.fromFile(file)) {
        source => source.getLines().mkString("\n")
      })

    val cmakePrebuild = new CMakePrebuildCachedSource(config.programs.cmake,
      programRunner,
      cacheDirectory,
      temporaryDirectory)
    val cmakeBuild = new CMakeBuildCachedSource(config.programs.cmake,
      programRunner,
      temporaryDirectory)
    val cmakeInstall = new CMakeInstallCachedSource(config.programs.cmake,
      programRunner,
      temporaryDirectory)
    val detailsWriter = new ProjectSourceDetailsWriterToCache(cacheDirectory, writeDataToFile)

    new CachedSourceCompile(detailsReader, cmakePrebuild, cmakeBuild, cmakeInstall, detailsWriter)
  }

  def createCachedArtefactAcquire(config : Configuration) : ProjectArtefactAcquire = {
    val cacheDirectory = new File(config.cache)

    val install = new ProjectArtefactInstallFromCache(cacheDirectory,
      workingDirectory.getAbsoluteFile,
      (src, dst) => {
        if (src.exists()) {
          Log("%s copied to %s".format(src, dst))
          if (!dst.exists())
            dst.mkdirs()

          FileUtils.copyDirectory(src, dst)
        }
        else
          Log("%s copy to %s failed".format(src, dst))
      })

    val write = new ProjectArtefactVersionWriterToLocal(workingDirectory, writeDataToFile)

    new CachedArtefactAcquire(install, write)
  }
}
