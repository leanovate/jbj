package de.leanovate.jbj.runtime.exception

import de.leanovate.jbj.api.http.JbjException

final case class ExitJbjException(message: Option[String]) extends JbjException(message.getOrElse("")) {
}
