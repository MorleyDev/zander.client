package uk.co.morleydev.zander.client.test.unit.data.fs

import com.lambdaworks.jacks.JacksMapper
import java.io.File
import org.mockito.Mockito
import uk.co.morleydev.zander.client.data.fs.WriteProjectArtefactVersionToLocal
import uk.co.morleydev.zander.client.model.store.ArtefactDetails
import uk.co.morleydev.zander.client.test.gen.{GenNative, GenModel}
import uk.co.morleydev.zander.client.test.unit.UnitTest

class ProjectArtefactVersionWriterToLocalTests extends UnitTest {
  describe("Given a working directory when writing a project/compiler/mode/version"){

    val mockFileWriter = mock[(String, File) => Unit]

    val workingDirectory = new File("some_working_dir")
    val writer = new WriteProjectArtefactVersionToLocal(workingDirectory, mockFileWriter)

    val project = GenModel.arg.genProject()
    val compiler = GenModel.arg.genCompiler()
    val buildMode = GenModel.arg.genBuildMode()
    val version = GenModel.store.genSourceVersion()

    val expectedFiles = GenNative.genSequence(1, 10, () => GenNative.genAlphaNumericString(1, 20))
    val expectedJson = JacksMapper.writeValueAsString[ArtefactDetails](new ArtefactDetails(version.value, expectedFiles))

    writer.apply(project, compiler, buildMode, version, expectedFiles)

    it("Then the expected json is written to the expected file") {
      val expectedWorkingDirectory = new File(workingDirectory, "%s.%s.%s.json".format(project, compiler, buildMode))

      Mockito.verify(mockFileWriter).apply(expectedJson, expectedWorkingDirectory)
    }
  }
}
