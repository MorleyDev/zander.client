package uk.co.morleydev.zander.client.data

import uk.co.morleydev.zander.client.model.Arguments

trait ArgumentParser extends (IndexedSeq[String] => Arguments)
