package uk.co.morleydev.zander.client.data

import uk.co.morleydev.zander.client.model.Arguments

trait ArgumentParserWithErrorCodes extends (IndexedSeq[String] => Either[Int, Arguments])
