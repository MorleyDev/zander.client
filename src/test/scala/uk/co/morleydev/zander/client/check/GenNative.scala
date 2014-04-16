package uk.co.morleydev.zander.client.check

import scala.util.Random

object GenNative {

  private val random = new Random()

  def genAlphaNumericString(minLength : Int, maxLength : Int) : String =
    random.alphanumeric.take(random.nextInt(maxLength - minLength + 1) + minLength).mkString

  def genAlphaNumericStringExcluding(minLength : Int, maxLength : Int, exclude : Seq[String]) : String =
    Iterator.continually(GenNative.genAlphaNumericString(minLength, maxLength))
    .find(s => !exclude.contains(s))
    .get

  def genOneOf[S](data : S*) : S =
    data(random.nextInt(data.size))

  def genOneFrom[S](data : Seq[S]) : S =
    data(random.nextInt(data.size))

  def genInt(minValue : Int, maxValue : Int) : Int =
    random.nextInt(maxValue - minValue + 1) + minValue
}
