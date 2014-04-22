package uk.co.morleydev.zander.client.test.unit.data.map

import org.scalatest.FunSpec
import uk.co.morleydev.zander.client.data.map.RemoveOverlappingFilesFromArtefactDetails
import uk.co.morleydev.zander.client.model.arg.BuildCompiler._
import uk.co.morleydev.zander.client.model.arg.BuildMode._
import uk.co.morleydev.zander.client.model.arg.{BuildMode, BuildCompiler, Project}
import uk.co.morleydev.zander.client.model.store.ArtefactDetails

class RemoveOverlappingFilesFromArtefactDetailsTests extends FunSpec {
  describe("Given a map of project details to artefact details") {

    val removeOverlapping = RemoveOverlappingFilesFromArtefactDetails

    describe("When removing overlapping files") {

      val mapWithOverlapping = Map[(Project, BuildCompiler, BuildMode), ArtefactDetails](
        (new Project("asdasd"), BuildCompiler.GnuCxx, BuildMode.Debug)
          -> new ArtefactDetails("some1", Seq[String]("file1", "file2", "file3")),

        (new Project("asdafw"), BuildCompiler.VisualStudio10, BuildMode.Debug)
          -> new ArtefactDetails("some2", Seq[String]("file2", "file3", "file4")),

        (new Project("asdafw"), BuildCompiler.VisualStudio10, BuildMode.Release)
          -> new ArtefactDetails("some3", Seq[String]("file1", "file5", "file3", "file6"))
      )

      val expectedResult = Map[(Project, BuildCompiler, BuildMode), ArtefactDetails](
        (new Project("asdasd"), BuildCompiler.GnuCxx, BuildMode.Debug)
          -> new ArtefactDetails("some1", Seq[String]()),

        (new Project("asdafw"), BuildCompiler.VisualStudio10, BuildMode.Debug)
          -> new ArtefactDetails("some2", Seq[String]("file4")),

        (new Project("asdafw"), BuildCompiler.VisualStudio10, BuildMode.Release)
          -> new ArtefactDetails("some3", Seq[String]("file5", "file6"))
      )

      val result = removeOverlapping.apply(mapWithOverlapping)

      it("Then the expected result is returned") {
        assert(expectedResult == result)
      }
    }
  }
}
