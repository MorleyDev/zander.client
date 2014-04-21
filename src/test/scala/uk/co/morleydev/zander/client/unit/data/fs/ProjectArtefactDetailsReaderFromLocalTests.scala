package uk.co.morleydev.zander.client.unit.data.fs

import org.scalatest.FunSpec
import org.scalatest.mock.MockitoSugar
import java.io.File
import uk.co.morleydev.zander.client.gen.{GenModel, GenNative}
import org.mockito.{Matchers, Mockito}
import com.lambdaworks.jacks.JacksMapper
import uk.co.morleydev.zander.client.model.store.ArtefactDetails
import uk.co.morleydev.zander.client.data.fs.ProjectArtefactDetailsReaderFromCache

class ProjectArtefactDetailsReaderFromLocalTests extends FunSpec with MockitoSugar {
  describe("Given a project artefact details reader") {

    val workingDirectory = new File("wd")
    val mockFileToStringReader = mock[File => String]
    val reader = new ProjectArtefactDetailsReaderFromCache(workingDirectory, mockFileToStringReader)

    describe("When reading from the working directory") {

      val expectedResultVersion = GenNative.genAsciiString(10, 100)
      Mockito.when(mockFileToStringReader.apply(Matchers.any[File]))
        .thenReturn(JacksMapper.writeValueAsString[ArtefactDetails](new ArtefactDetails(expectedResultVersion)))

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
    }
  }
}
