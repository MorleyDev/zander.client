package uk.co.morleydev.zander.client

import uk.co.morleydev.zander.client.model.{Arguments, Configuration}
import uk.co.morleydev.zander.client.validator.Validator

class Program(projectValidator : Validator[String]) {
  def run(args : Arguments, config : Configuration) : Int = {
    projectValidator.validate(args.project)
    -404
  }
}
