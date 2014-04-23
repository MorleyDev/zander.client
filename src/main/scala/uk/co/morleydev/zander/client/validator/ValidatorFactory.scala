package uk.co.morleydev.zander.client.validator

trait ValidatorFactory {
  def createValidateArtefactDetailsDoNotExist() : ValidateArtefactDetailsExistence
  def createValidateArtefactDetailsExist() : ValidateArtefactDetailsExistence
}


