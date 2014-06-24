package uk.co.morleydev.zander.client.data

import uk.co.morleydev.zander.client.model.Configuration

trait LoadOrCreateConfiguration extends (String => Configuration)