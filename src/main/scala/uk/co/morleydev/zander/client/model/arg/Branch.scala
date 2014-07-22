package uk.co.morleydev.zander.client.model.arg

class Branch(val value : String) {
  override def equals(other : Any) : Boolean = {
    other match {
      case branch: Branch => value.equals(branch.value)
      case _ => false
    }
  }

  override def toString = value.toString
}
