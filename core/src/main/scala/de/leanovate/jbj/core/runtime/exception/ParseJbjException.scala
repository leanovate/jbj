package de.leanovate.jbj.core.runtime.exception

import de.leanovate.jbj.core.ast.FileNodePosition
import de.leanovate.jbj.api.JbjException

class ParseJbjException(var msg: String, var pos: FileNodePosition) extends JbjException(msg) {

}
