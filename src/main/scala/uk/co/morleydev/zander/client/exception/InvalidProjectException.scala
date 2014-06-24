package uk.co.morleydev.zander.client.exception

/**
 * Created by jason on 24/06/14.
 */
class InvalidProjectException(val project : String) extends InvalidArgumentsException("Project " + project + " is not valid project")
