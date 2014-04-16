package uk.co.morleydev.zander.client.model.arg

class Project(val value : String) {
  require(value.size <= 20 && value.size >= 1 && value.forall(c => c.isLetterOrDigit || c == '-' || c == '_'))

  override def equals(other : Any) : Boolean = {
    other match {
      case project: Project => value.equals(project.value)
      case _ => false
    }
  }

  override def toString = value.toString
}
