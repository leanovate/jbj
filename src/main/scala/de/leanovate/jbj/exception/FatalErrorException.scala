package de.leanovate.jbj.exception

class FatalErrorException(msg: String, cause: Throwable) extends JbjException(msg, cause) {
  def this(msg: String) = this(msg, null)

}
