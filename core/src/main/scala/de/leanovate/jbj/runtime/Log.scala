package de.leanovate.jbj.runtime

import java.io.PrintStream
import de.leanovate.jbj.ast.FilePosition

class Log(out: PrintStream, err: PrintStream) {
  def fatal(position: FilePosition, msg: String) {
    err.println("PHP Fatal error: %s in %s on line %d".format(msg, position.fileName, position.line))
    out.println("Fatal error: %s in %s on line %d".format(msg, position.fileName, position.line))
  }

  def warn(position: FilePosition, msg: String) {
    err.println("PHP Warning: %s in %s on line %d".format(msg, position.fileName, position.line))
    out.println("Warning: %s in %s on line %d".format(msg, position.fileName, position.line))
  }

  def notice(position: FilePosition, msg: String) {
    err.println("PHP Notice: %s in %s on line %d".format(msg, position.fileName, position.line))
    out.println("Notice: %s in %s on line %d".format(msg, position.fileName, position.line))
  }
}
