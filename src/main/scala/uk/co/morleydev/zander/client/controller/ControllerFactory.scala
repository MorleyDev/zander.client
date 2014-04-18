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

trait ControllerFactory {
  def createInstallController(config : Configuration) : Controller
}

class ControllerFactoryImpl(processBuilderFactory : NativeProcessBuilderFactory,
                            temporaryDirectory : File) extends ControllerFactory {
  def createInstallController(config : Configuration) : Controller = {

    val getProjectRemote = new GetProjectDtoRemote(new URL(config.server))
    val programRunner = new LocalProgramRunner(processBuilderFactory)
    val cacheDirectory = new File(config.cache)
    val gitDownloadRemote = new GitDownloadRemote(config.programs.git,
      programRunner,
      cacheDirectory)
    val cmakePrebuild = new CMakePrebuildLocal(config.programs.cmake,
      programRunner,
      cacheDirectory,
      temporaryDirectory)
    val cmakeBuild = new CMakeBuildLocal(config.programs.cmake,
      programRunner,
      temporaryDirectory)
    val cmakeInstall = new CMakeInstallLocal(config.programs.cmake,
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
      gitDownloadRemote,
      cmakePrebuild,
      cmakeBuild,
      cmakeInstall,
      artefactInstall)
  }
}
