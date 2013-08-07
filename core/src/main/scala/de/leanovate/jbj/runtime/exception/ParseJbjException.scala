package de.leanovate.jbj.runtime.exception

import de.leanovate.jbj.ast.FileNodePosition

class ParseJbjException(var msg: String, var pos: FileNodePosition) extends JbjException(msg) {

}
