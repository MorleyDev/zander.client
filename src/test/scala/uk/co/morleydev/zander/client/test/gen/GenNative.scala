package uk.co.morleydev.zander.client.test.gen

import scala.util.Random
import java.net.URL
import java.io.{ByteArrayInputStream, InputStream}

object GenNative {

  private val random = new Random()

  val alphaNumericCharacters : Seq[Char] = ('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')
  val asciiCharacters : Seq[Char] = (0 to 255).map(_.toChar).toSeq
  val nonAsciiCharacters : Seq[Char] =
    Iterator.continually(random.nextString(1))
    .dropWhile(s => s(0).toInt < 256)
    .map(s => s(0))
    .take(20)
    .toSeq

  def genAsciiString(minLength : Int, maxLength : Int) : String =
    genStringContaining(minLength, maxLength, asciiCharacters)

  def genAlphaNumericString(minLength : Int, maxLength : Int) : String =
    genStringContaining(minLength, maxLength, alphaNumericCharacters)

  def genAlphaNumericStringExcluding(minLength : Int, maxLength : Int, exclude : Seq[String]) : String =
    Iterator.continually(GenNative.genAlphaNumericString(minLength, maxLength))
    .find(s => !exclude.contains(s))
    .get

  def genStringContaining(minLength : Int, maxLength : Int, chars : Seq[Char]) : String =
    Iterator.continually(chars(random.nextInt(chars.size)))
      .take(random.nextInt(maxLength - minLength + 1) + minLength)
      .mkString

  def genOneOf[S](data : S*) : S =
    data(random.nextInt(data.size))

  def genOneFrom[S](data : Seq[S]) : S =
    data(random.nextInt(data.size))

  def genInt(minValue : Int, maxValue : Int) : Int =
    random.nextInt(maxValue - minValue + 1) + minValue

  def genIntExcluding(minValue : Int, maxValue : Int, exclude : Seq[Int]) : Int =
    Iterator.continually(random.nextInt(maxValue - minValue + 1) + minValue)
            .dropWhile(exclude.contains(_))
            .take(1).toList.head

  def genHttpUrl() : URL =
    new URL("http://" + genAlphaNumericString(3, 20) + genOneOf(".co.uk", ".com", ".net", ".org"))

  def genSequence[T](minSize : Int, maxSize : Int, gen : (() => T)) : Seq[T] =
    Iterator.continually[T](gen())
            .take(genInt(minSize, maxSize))
            .toSeq

  def genInputStreamString() : InputStream =
    new ByteArrayInputStream(Iterator.continually(GenNative.genAlphaNumericString(10, 100))
      .take(genInt(1, 10))
      .mkString("\n")
      .getBytes("UTF-8"))
}
