package uk.co.morleydev.zander.client.validator

import uk.co.morleydev.zander.client.validator.exceptions.InvalidProjectException

object ProjectValidator extends Validator[String] {
  def validate(value: String) {
    if (!value.forall(_.isLetterOrDigit) || value.size == 0 || value.size > 20)
      throw new InvalidProjectException
  }
}
