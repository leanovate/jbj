package de.leanovate.jbj.core.runtime.exception

import de.leanovate.jbj.api.JbjException

case class ExitJbjException(message: String) extends JbjException(message) {
}
