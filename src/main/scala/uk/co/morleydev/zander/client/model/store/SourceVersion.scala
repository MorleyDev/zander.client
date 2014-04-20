package uk.co.morleydev.zander.client.model.store

class SourceVersion(val value : String) {

  override def equals(other : Any) : Boolean = {
    other match {
      case version: SourceVersion => value.equals(version.value)
      case _ => false
    }
  }

  override def toString : String = value

}
