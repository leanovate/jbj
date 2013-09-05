package de.leanovate.jbj.runtime.exception

import de.leanovate.jbj.api.JbjException

final case class ExitJbjException(message: String) extends JbjException(message) {
}
