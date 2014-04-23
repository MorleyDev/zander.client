package uk.co.morleydev.zander.client.validator.impl

import java.io.File
import uk.co.morleydev.zander.client.data.NativeProcessBuilderFactory
import uk.co.morleydev.zander.client.data.impl.DataFactoryImpl
import uk.co.morleydev.zander.client.validator.{ValidatorFactory, ValidateArtefactDetailsExistence}

class ValidatorFactoryImpl(processBuilderFactory : NativeProcessBuilderFactory,
                            temporaryDirectory : File,
                            workingDirectory : File) extends ValidatorFactory {
                             
                               private val dataFactory = new DataFactoryImpl(processBuilderFactory, temporaryDirectory, workingDirectory)

                               override def createValidateArtefactDetailsDoNotExist(): ValidateArtefactDetailsExistence =
                                 new ValidateArtefactDetailsDoNotExist(dataFactory.createCheckArtefactDetailsExist())

                               override def createValidateArtefactDetailsExist(): ValidateArtefactDetailsExistence =
                                 new ValidateArtefactDetailsExist(dataFactory.createCheckArtefactDetailsExist())
                             }
