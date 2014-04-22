package uk.co.morleydev.zander.client.test.unit.service.impl

import org.scalatest.FunSpec
import org.scalatest.mock.MockitoSugar
import uk.co.morleydev.zander.client.service.impl.GetAllProjectArtefactDetailsFromLocal
import java.io.File
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.model.arg.BuildCompiler._
import uk.co.morleydev.zander.client.model.arg.BuildMode._
import uk.co.morleydev.zander.client.data.ReadProjectArtefactDetails
import org.mockito.{Matchers, Mockito}
import uk.co.morleydev.zander.client.test.gen.{GenNative, GenModel}
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock
import uk.co.morleydev.zander.client.model.store.ArtefactDetails

class GetAllProjectArtefactDetailsFromLocalTests extends FunSpec with MockitoSugar {
  describe("Given a working directory") {

    val mockGetFilesInDirectory = mock[(File, String) => Seq[File]]
    val mockGetDetailsFromFilename = mock[(String => (Project, BuildCompiler, BuildMode))]
    val mockReadProjectArtefactDetails = mock[ReadProjectArtefactDetails]

    val workingDirectory = new File("working")
    val getAllDetails = new GetAllProjectArtefactDetailsFromLocal(workingDirectory,
      mockGetFilesInDirectory,
      mockGetDetailsFromFilename,
      mockReadProjectArtefactDetails)

    describe("When getting all artefact details from the local directory") {

      val validFiles = Seq[File](new File("z.a.a.json"), new File("b.c.d.json"), new File("c.e.f.g.json"))
      val invalidFiles = Seq[File](new File("z.a.json"), new File("b.d.json"), new File("g.json"))
      val illegalArgumentFiles = Seq[File](new File("p.q.t.json"))
      val noSuchElementFiles = Seq[File](new File("t.a.t.json"))
      Mockito.when(mockGetFilesInDirectory.apply(Matchers.any[File], Matchers.any[String]))
             .thenReturn(validFiles ++ invalidFiles ++ illegalArgumentFiles ++ noSuchElementFiles)

      val expectedProjectDetails = GenNative.genSequence(validFiles.size, validFiles.size,
                                                () => GenModel.arg.genProjectCompilerBuildModeTuple()).toIndexedSeq
      var i = 0
      validFiles.foreach(f => Mockito.when(mockGetDetailsFromFilename.apply(f.getName))
                .thenReturn(expectedProjectDetails({val j = i; i += 1; j})))
      illegalArgumentFiles.foreach(f => Mockito.when(mockGetDetailsFromFilename.apply(f.getName))
        .thenAnswer(new Answer[(Project, BuildCompiler, BuildMode)] {
        override def answer(invocation: InvocationOnMock): (Project, BuildCompiler, BuildMode) = throw new IllegalArgumentException
      }))
      noSuchElementFiles.foreach(f => Mockito.when(mockGetDetailsFromFilename.apply(f.getName))
        .thenAnswer(new Answer[(Project, BuildCompiler, BuildMode)] {
        override def answer(invocation: InvocationOnMock): (Project, BuildCompiler, BuildMode) = throw new NoSuchElementException
      }))

      val expectedResult = expectedProjectDetails.map(d => (d, GenModel.store.genArtefactDetails())).toMap
      expectedProjectDetails.foreach(f => Mockito.when(mockReadProjectArtefactDetails.apply(f._1, f._2, f._3))
        .thenReturn(expectedResult(f)))

      val result = getAllDetails.apply()

      it("Then the json files in the working directory are retrieved") {
        Mockito.verify(mockGetFilesInDirectory).apply(workingDirectory, "json")
      }
      it("Then the valid files are converted to details") {
        (validFiles ++ illegalArgumentFiles ++ noSuchElementFiles)
          .foreach(f => Mockito.verify(mockGetDetailsFromFilename).apply(f.getName))
      }
      it("Then the artefact details are read") {
        expectedProjectDetails.foreach(f => mockReadProjectArtefactDetails(f._1, f._2, f._3))
      }
      it("Then the expected result is returned") {
        assert(result == expectedResult)
      }
    }
  }
}
