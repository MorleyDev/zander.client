package uk.co.morleydev.zander.client

import uk.co.morleydev.zander.client.model.OperationArguments

package object controller {
  type Controller = ((OperationArguments) => Unit)
}
