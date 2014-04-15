package uk.co.morleydev.zander.client.validator

trait Validator[S] {
  def validate(value : S)
}
