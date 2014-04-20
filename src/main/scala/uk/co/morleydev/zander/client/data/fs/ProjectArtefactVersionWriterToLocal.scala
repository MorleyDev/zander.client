package uk.co.morleydev.zander.client.data.fs

import uk.co.morleydev.zander.client.data.ProjectArtefactVersionWriter
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.Compiler.Compiler
import uk.co.morleydev.zander.client.model.arg.BuildMode.BuildMode
import java.io.File
import com.lambdaworks.jacks.JacksMapper
import uk.co.morleydev.zander.client.model.store.{SourceVersion, InstalledArtefactDetails}

class ProjectArtefactVersionWriterToLocal(workingDirectory : File,
                                          writeStringToFile : ((String, File) => Unit)) extends ProjectArtefactVersionWriter {
  override def apply(p : Project, c : Compiler, b : BuildMode, v : SourceVersion) : Unit = {

    val versionJson = JacksMapper.writeValueAsString[InstalledArtefactDetails](new InstalledArtefactDetails(v.value))

    writeStringToFile(versionJson, new File(workingDirectory, "%s.%s.%s.json".format(p,c,b)))
  }
}
