package uk.co.morleydev.zander.client.exception

/**
 * Created by jason on 24/06/14.
 */
class InvalidBuildModeException(val mode : String) extends InvalidArgumentsException("BuildMode " + mode + " is not valid build mode")
