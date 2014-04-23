package uk.co.morleydev.zander.client.test.unit.data.fs

import com.lambdaworks.jacks.JacksMapper
import java.io.File
import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.data.fs.ReadProjectArtefactDetailsFromLocal
import uk.co.morleydev.zander.client.model.store.ArtefactDetails
import uk.co.morleydev.zander.client.test.gen.{GenModel, GenNative}
import uk.co.morleydev.zander.client.test.unit.UnitTest

class ProjectArtefactDetailsReaderFromLocalTests extends UnitTest {
  describe("Given a project artefact details reader") {

    val workingDirectory = new File("wd")
    val mockFileToStringReader = mock[File => String]
    val reader = new ReadProjectArtefactDetailsFromLocal(workingDirectory, mockFileToStringReader)

    describe("When reading from the working directory") {

      val expectedResultVersion = GenNative.genAsciiString(10, 100)
      val expectedResultFiles = GenNative.genSequence(1, 10, () => GenNative.genAlphaNumericString(1, 20))
      Mockito.when(mockFileToStringReader.apply(Matchers.any[File]))
        .thenReturn(JacksMapper.writeValueAsString[ArtefactDetails](new ArtefactDetails(expectedResultVersion,
        expectedResultFiles)))

      val project = GenModel.arg.genProject()
      val compiler = GenModel.arg.genCompiler()
      val mode = GenModel.arg.genBuildMode()

      val result = reader(project, compiler, mode)

      it("Then the source details are read from the expected file") {
        Mockito.verify(mockFileToStringReader).apply(new File(workingDirectory, "%s.%s.%s.json".format(project, compiler, mode)))
      }
      it("Then the expected Source Details are returned") {
        assert(result.version == expectedResultVersion)
      }
      it("Then the expected Source Files are returned") {
        assert(result.files == expectedResultFiles)
      }
    }
  }
}
