package uk.co.morleydev.zander.client.exception

/**
 * Created by jason on 24/06/14.
 */
class InvalidCompilerException(val compiler : String) extends InvalidArgumentsException("Compiler " + compiler + " is not valid compiler")
