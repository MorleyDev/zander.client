package uk.co.morleydev.zander.client.service

import uk.co.morleydev.zander.client.data.NativeProcessBuilderFactory
import java.io.{PrintWriter, File}
import uk.co.morleydev.zander.client.data.program._
import uk.co.morleydev.zander.client.service.impl.{CachedArtefactAcquire, CachedSourceCompile, CachedSourceAcquire}
import uk.co.morleydev.zander.client.model.Configuration
import uk.co.morleydev.zander.client.data.fs.{ProjectArtefactVersionWriterToLocal, ProjectArtefactInstallFromCache}
import uk.co.morleydev.zander.client.util.Log
import uk.co.morleydev.zander.client.util.Using.using
import org.apache.commons.io.FileUtils

class ServiceFactory(processBuilderFactory : NativeProcessBuilderFactory,
                     temporaryDirectory : File,
                     workingDirectory : File) {

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

    new CachedSourceCompile(cmakePrebuild, cmakeBuild, cmakeInstall)
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

    val write = new ProjectArtefactVersionWriterToLocal(workingDirectory, (data, file) => {
      println("Writing " + data + " to " + file.getPath)
      using(new PrintWriter(file)) { print =>
        print.write(data)
      }
    })

    new CachedArtefactAcquire(install, write)
  }
}
