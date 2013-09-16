/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime

import java.io.PrintStream
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.api.JbjSettings
import de.leanovate.jbj.api.JbjSettings.{DisplayError, ErrorLevel}
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.output.OutputBuffer
import de.leanovate.jbj.runtime.value.IntegerVal

class Log(context: Context, out: OutputBuffer, err: Option[PrintStream]) {
  var silent: Boolean = false

  def fatal(msg: String) {
    stdLog(JbjSettings.ErrorLevel.E_ERROR, "Fatal error", msg)
  }

  def catchableFatal(msg: String): Boolean = {
    stdLog(JbjSettings.ErrorLevel.E_RECOVERABLE_ERROR, "Catchable fatal error", msg)
  }

  def compileError(msg: String) {
    if (!handleError(JbjSettings.ErrorLevel.E_COMPILE_ERROR, msg, context.currentPosition) && !silent &&
      context.settings.getErrorReporting.contains(JbjSettings.ErrorLevel.E_COMPILE_ERROR)) {
      err.foreach(_.println("PHP Compile error: %s in %s on line %d".format(msg, context.currentPosition.fileName, context.currentPosition.line)))
      if (context.settings.getDisplayErrors == DisplayError.STDOUT) {
        out.println()
        out.println("Compile error: %s in %s on line %d".format(msg, context.currentPosition.fileName, context.currentPosition.line))
      }
    }
  }

  def warn(msg: String) {
    stdLog(JbjSettings.ErrorLevel.E_WARNING, "Warning", msg)
  }

  def notice(msg: String) {
    stdLog(JbjSettings.ErrorLevel.E_NOTICE, "Notice", msg)
  }

  def strict(msg: String) {
    stdLog(JbjSettings.ErrorLevel.E_STRICT, "Strict Standards", msg)
  }

  def parseError(position: FileNodePosition, msg: String) {
    if (!silent && context.settings.getErrorReporting.contains(JbjSettings.ErrorLevel.E_PARSE)) {
      err.foreach(_.println("PHP Parse error: %s in %s on line %d".format(msg, position.fileName, position.line)))
      if (context.settings.getDisplayErrors == DisplayError.STDOUT) {
        out.println()
        out.println("Parse error: %s in %s on line %d".format(msg, position.fileName, position.line))
      }
    }
  }

  def deprecated(position: NodePosition, msg: String) {
    if (!silent && context.settings.getErrorReporting.contains(JbjSettings.ErrorLevel.E_DEPRECATED)) {
      err.foreach(_.println("PHP Deprecated: %s in %s on line %d".format(msg, position.fileName, position.line)))
      if (context.settings.getDisplayErrors == DisplayError.STDOUT) {
        out.println()
        out.println("Deprecated: %s in %s on line %d".format(msg, position.fileName, position.line))
      }
    }
  }

  private def stdLog(errorLevel: ErrorLevel, prefix: String, msg: String): Boolean = {
    val message = "%s: %s in %s on line %d".format(prefix, msg, context.currentPosition.fileName, context.currentPosition.line)
    if (handleError(errorLevel, msg, context.currentPosition))
      false
    else {
      if (!silent && context.settings.getErrorReporting.contains(errorLevel)) {
        err.foreach(_.println("PHP %s".format(message)))
        if (context.settings.getDisplayErrors == DisplayError.STDOUT) {
          out.println()
          out.println(message)
        }
      }
      if (context.settings.isTrackErrors) {
        context.defineVariable("php_errormsg", PVar(StringVal(msg)(context)))
      }
      true
    }
  }

  private def handleError(errorLevel: ErrorLevel, msg: String, position: NodePosition): Boolean = {
    implicit val global = context.global
    global.errorHandler.exists {
      case errorHandler if (context.global.errorHandlerTypes & errorLevel.getValue) != 0 =>
        CallbackHelper.callCallabck(errorHandler, IntegerVal(errorLevel.getValue), StringVal(msg),
          StringVal(position.fileName), IntegerVal(position.line), ArrayVal()).asVal match {
          case BooleanVal.FALSE => false
          case _ => true
        }
      case _ =>
        false
    }
  }
}
