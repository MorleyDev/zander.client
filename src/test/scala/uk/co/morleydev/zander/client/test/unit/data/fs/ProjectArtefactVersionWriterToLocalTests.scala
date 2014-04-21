package uk.co.morleydev.zander.client.test.unit.data.fs

import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.data.fs.ProjectArtefactVersionWriterToLocal
import java.io.File
import uk.co.morleydev.zander.client.test.gen.GenModel
import com.lambdaworks.jacks.JacksMapper
import uk.co.morleydev.zander.client.model.store.ArtefactDetails
import org.mockito.Mockito

class ProjectArtefactVersionWriterToLocalTests extends FunSpec with MockitoSugar {
  describe("Given a working directory when writing a project/compiler/mode/version"){

    val mockFileWriter = mock[(String, File) => Unit]
    val workingDirectory = new File("some_working_dir")

    val writer = new ProjectArtefactVersionWriterToLocal(workingDirectory, mockFileWriter)

    val project = GenModel.arg.genProject()
    val compiler = GenModel.arg.genCompiler()
    val buildMode = GenModel.arg.genBuildMode()
    val version = GenModel.store.genSourceVersion()

    val expectedJson = JacksMapper.writeValueAsString[ArtefactDetails](new ArtefactDetails(version.value))

    writer.apply(project, compiler, buildMode, version)

    it("Then the expected json is written to the expected file") {
      val expectedWorkingDirectory = new File(workingDirectory, "%s.%s.%s.json".format(project, compiler, buildMode))

      Mockito.verify(mockFileWriter).apply(expectedJson, expectedWorkingDirectory)
    }
  }
}
