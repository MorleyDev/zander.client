package uk.co.morleydev.zander.client.controller

import uk.co.morleydev.zander.client.data.net.GetProjectDtoRemote
import uk.co.morleydev.zander.client.model.Configuration
import java.net.URL
import uk.co.morleydev.zander.client.data.program._
import uk.co.morleydev.zander.client.data.NativeProcessBuilderFactory
import java.io.File
import uk.co.morleydev.zander.client.model.Configuration
import uk.co.morleydev.zander.client.data.fs.ProjectArtefactInstallFromCache
import org.apache.commons.io.FileUtils
import uk.co.morleydev.zander.client.util.Log
import uk.co.morleydev.zander.client.service.impl.CachedSourceAcquire

trait ControllerFactory {
  def createInstallController(config : Configuration) : Controller
}

class ControllerFactoryImpl(processBuilderFactory : NativeProcessBuilderFactory,
                            temporaryDirectory : File) extends ControllerFactory {
  def createInstallController(config : Configuration) : Controller = {

    val getProjectRemote = new GetProjectDtoRemote(new URL(config.server))
    val programRunner = new LocalProgramRunner(processBuilderFactory)
    val cacheDirectory = new File(config.cache)
    val gitDownloadRemote = new GitDownloadSourceToCache(config.programs.git,
      programRunner,
      cacheDirectory)
    val gitUpdateRemote = new GitUpdateCachedSource(config.programs.git,
      cacheDirectory,
      programRunner)
    val sourceAcquire = new CachedSourceAcquire(cacheDirectory,
      f => f.exists() && f.isDirectory,
      gitDownloadRemote,
      gitUpdateRemote)
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
    println(new File("").getAbsoluteFile.toString)
    val artefactInstall = new ProjectArtefactInstallFromCache(cacheDirectory,
      new File("").getAbsoluteFile,
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

    new InstallController(getProjectRemote,
      sourceAcquire,
      cmakePrebuild,
      cmakeBuild,
      cmakeInstall,
      artefactInstall)
  }
}
