package uk.co.morleydev.zander.client.gen

import scala.util.Random
import java.net.URL

object GenNative {

  private val random = new Random()

  def genAlphaNumericString(minLength : Int, maxLength : Int) : String =
    random.alphanumeric.take(random.nextInt(maxLength - minLength + 1) + minLength).mkString

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

  def genHttpUrl() : URL =
    new URL("http://" + genAlphaNumericString(3, 20) + genOneOf(".co.uk", ".com", ".net", ".org"))
}
