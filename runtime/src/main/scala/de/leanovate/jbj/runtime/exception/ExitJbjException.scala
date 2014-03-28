package de.leanovate.jbj.runtime.exception

import de.leanovate.jbj.api.http.JbjException

final case class ExitJbjException(exitCode:Int, message: Option[String]) extends JbjException(message.getOrElse("")) {
}
