package uk.co.morleydev.zander.client.data.fs

import java.io.File
import uk.co.morleydev.zander.client.data.ListProjectCacheFiles
import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import uk.co.morleydev.zander.client.model.arg.Project

class ListProjectCacheFilesInCache(cache : File, listFiles : (File) => Seq[File])
  extends ListProjectCacheFiles {

  override def apply(project: Project, compiler: BuildCompiler, mode: BuildMode): Seq[String] = {

    val cacheArtefactDir = new File(cache, "%s/%s.%s".format(project, compiler, mode))

    val includeDir = new File(cacheArtefactDir, "include")
    val libDir = new File(cacheArtefactDir, "lib")
    val binDir = new File(cacheArtefactDir, "bin")

    val includeFiles = listFiles(includeDir)
    val libFiles = listFiles(libDir)
    val binFiles = listFiles(binDir)

    (includeFiles ++ libFiles ++ binFiles)
      .map(f => f.getAbsolutePath.diff(cacheArtefactDir.getAbsolutePath).substring(1))
      .toSeq
  }
}
