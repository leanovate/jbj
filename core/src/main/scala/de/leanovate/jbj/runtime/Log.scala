package de.leanovate.jbj.runtime

import java.io.PrintStream
import de.leanovate.jbj.ast.{NodePosition, FileNodePosition}
import de.leanovate.jbj.runtime.context.Context

class Log(context:Context, out: PrintStream, err: PrintStream) {
  def fatal(msg: String) {
    if ((context.settings.errorReporting & Settings.E_ERROR) != 0) {
      err.println("PHP Fatal error: %s in %s on line %d".format(msg, context.currentPosition.fileName, context.currentPosition.line))
      out.println()
      out.println("Fatal error: %s in %s on line %d".format(msg, context.currentPosition.fileName, context.currentPosition.line))
    }
  }

  def compileError(msg:String) {
    if ((context.settings.errorReporting & Settings.E_COMPILE_ERROR) != 0) {
      err.println("PHP Compile error: %s in %s on line %d".format(msg, context.currentPosition.fileName, context.currentPosition.line))
      out.println()
      out.println("Compile error: %s in %s on line %d".format(msg, context.currentPosition.fileName, context.currentPosition.line))
    }
  }

  def warn(msg: String) {
    if ((context.settings.errorReporting & Settings.E_WARNING) != 0) {
      err.println("PHP Warning: %s in %s on line %d".format(msg, context.currentPosition.fileName, context.currentPosition.line))
      out.println()
      out.println("Warning: %s in %s on line %d".format(msg, context.currentPosition.fileName, context.currentPosition.line))
    }
  }

  def notice(msg: String) {
    if ((context.settings.errorReporting & Settings.E_NOTICE) != 0) {
      err.println("PHP Notice: %s in %s on line %d".format(msg, context.currentPosition.fileName, context.currentPosition.line))
      out.println()
      out.println("Notice: %s in %s on line %d".format(msg, context.currentPosition.fileName, context.currentPosition.line))
    }
  }

  def strict(msg:String) {
    if ((context.settings.errorReporting & Settings.E_STRICT) != 0 ) {
      err.println("PHP Strict Standards: %s in %s on line %d".format(msg, context.currentPosition.fileName, context.currentPosition.line))
      out.println()
      out.println("Strict Standards: %s in %s on line %d".format(msg, context.currentPosition.fileName, context.currentPosition.line))
    }
  }
  def parseError(position:FileNodePosition, msg:String) {
    if ((context.settings.errorReporting & Settings.E_PARSE) != 0) {
      err.println("PHP Parse error: %s in %s on line %d".format(msg, position.fileName, position.line))
      out.println()
      out.println("Parse error: %s in %s on line %d".format(msg, position.fileName, position.line))
    }
  }

  def deprecated(position:NodePosition, msg:String) {
    if ((context.settings.errorReporting & Settings.E_DEPRECATED) != 0) {
      err.println("PHP Deprecated: %s in %s on line %d".format(msg, position.fileName, position.line))
      out.println()
      out.println("Deprecated: %s in %s on line %d".format(msg, position.fileName, position.line))
    }
  }
}
