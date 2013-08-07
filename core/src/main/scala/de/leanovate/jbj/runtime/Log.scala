package de.leanovate.jbj.runtime

import java.io.PrintStream
import de.leanovate.jbj.ast.NodePosition

class Log(settings: Settings, out: PrintStream, err: PrintStream) {
  def fatal(position: NodePosition, msg: String) {
    if ((settings.errorReporting & Settings.E_ERROR) != 0) {
      err.println("PHP Fatal error: %s in %s on line %d".format(msg, position.fileName, position.line))
      out.println()
      out.println("Fatal error: %s in %s on line %d".format(msg, position.fileName, position.line))
    }
  }

  def compileError(position:NodePosition, msg:String) {
    if ((settings.errorReporting & Settings.E_COMPILE_ERROR) != 0) {
      err.println("PHP Compile error: %s in %s on line %d".format(msg, position.fileName, position.line))
      out.println()
      out.println("Compile error: %s in %s on line %d".format(msg, position.fileName, position.line))
    }
  }
  def warn(position: NodePosition, msg: String) {
    if ((settings.errorReporting & Settings.E_WARNING) != 0) {
      err.println("PHP Warning: %s in %s on line %d".format(msg, position.fileName, position.line))
      out.println()
      out.println("Warning: %s in %s on line %d".format(msg, position.fileName, position.line))
    }
  }

  def notice(position: NodePosition, msg: String) {
    if ((settings.errorReporting & Settings.E_NOTICE) != 0) {
      err.println("PHP Notice: %s in %s on line %d".format(msg, position.fileName, position.line))
      out.println()
      out.println("Notice: %s in %s on line %d".format(msg, position.fileName, position.line))
    }
  }

  def parseError(position:NodePosition, msg:String) {
    if ((settings.errorReporting & Settings.E_PARSE) != 0) {
      err.println("PHP Parse error: %s in %s on line %d".format(msg, position.fileName, position.line))
      out.println()
      out.println("Parse error: %s in %s on line %d".format(msg, position.fileName, position.line))
    }
  }
}
