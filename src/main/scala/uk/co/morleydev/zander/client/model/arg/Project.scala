package uk.co.morleydev.zander.client.model.arg

class Project(val value : String) {
  require(value.size <= 20
    && value.size >= 1
    && value.forall(c => ('A' to 'Z').contains(c)
      || ('a' to 'z').contains(c)
      || ('0' to '9').contains(c)
      || c == '-'
      || c == '_'
      || c == '.'), "Should be alpha numeric or contain -_. but was " + value)

  override def equals(other : Any) : Boolean = {
    other match {
      case project: Project => value.equals(project.value)
      case _ => false
    }
  }

  override def toString = value.toString
}
