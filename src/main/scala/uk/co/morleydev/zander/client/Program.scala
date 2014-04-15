package uk.co.morleydev.zander.client

import uk.co.morleydev.zander.client.model.{Arguments, Configuration}
import uk.co.morleydev.zander.client.validator.Validator
import uk.co.morleydev.zander.client.validator.exceptions.InvalidProjectException

class Program(projectValidator : Validator[String]) {
  def run(args : Arguments, config : Configuration) : Int = {

    try {
      projectValidator.validate(args.project)
    } catch {
      case e : InvalidProjectException => return 2
    }
    -404
  }
}
