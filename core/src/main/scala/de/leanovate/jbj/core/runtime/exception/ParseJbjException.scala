package de.leanovate.jbj.core.runtime.exception

import de.leanovate.jbj.core.ast.FileNodePosition

class ParseJbjException(var msg: String, var pos: FileNodePosition) extends JbjException(msg) {

}
