package de.leanovate.jbj.runtime

import java.io.PrintStream
import de.leanovate.jbj.ast.NodePosition

class Log(out: PrintStream, err: PrintStream) {
  def fatal(position: NodePosition, msg: String) {
    err.println("PHP Fatal error: %s in %s on line %d".format(msg, position.fileName, position.line))
    out.println()
    out.println("Fatal error: %s in %s on line %d".format(msg, position.fileName, position.line))
  }

  def warn(position: NodePosition, msg: String) {
    err.println("PHP Warning: %s in %s on line %d".format(msg, position.fileName, position.line))
    out.println()
    out.println("Warning: %s in %s on line %d".format(msg, position.fileName, position.line))
  }

  def notice(position: NodePosition, msg: String) {
    err.println("PHP Notice: %s in %s on line %d".format(msg, position.fileName, position.line))
    out.println()
    out.println("Notice: %s in %s on line %d".format(msg, position.fileName, position.line))
  }
}
