package uk.co.morleydev.zander.client.data.exception

/**
 * Created by jason on 24/06/14.
 */
class InvalidOperationException(val operation : String) extends InvalidArgumentsException("Operation " + operation + " is not valid operation")
