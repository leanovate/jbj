/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.runtime

import java.io.PrintStream
import de.leanovate.jbj.core.ast.{NodePosition, FileNodePosition}
import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.api.JbjSettings
import de.leanovate.jbj.core.runtime.output.OutputBuffer
import de.leanovate.jbj.api.JbjSettings.ErrorLevel
import de.leanovate.jbj.core.runtime.buildin.CallbackHelper
import de.leanovate.jbj.core.runtime.value.{StringVal, IntegerVal}

class Log(context: Context, out: OutputBuffer, err: Option[PrintStream]) {
  var silent: Boolean = false

  def fatal(msg: String) {
    if (!handleError(JbjSettings.ErrorLevel.E_ERROR, msg, context.currentPosition) && !silent &&
      context.settings.getErrorReporting.contains(JbjSettings.ErrorLevel.E_ERROR)) {
      err.foreach(_.println("PHP Fatal error: %s in %s on line %d".format(msg, context.currentPosition.fileName, context.currentPosition.line)))
      out.println()
      out.println("Fatal error: %s in %s on line %d".format(msg, context.currentPosition.fileName, context.currentPosition.line))
    }
  }

  def catchableFatal(msg: String) {
    if (!handleError(JbjSettings.ErrorLevel.E_ERROR, msg, context.currentPosition) && !silent &&
      context.settings.getErrorReporting.contains(JbjSettings.ErrorLevel.E_ERROR)) {
      err.foreach(_.println("PHP Catchable fatal error: %s in %s on line %d".format(msg, context.currentPosition.fileName, context.currentPosition.line)))
      out.println()
      out.println("Catchable fatal error: %s in %s on line %d".format(msg, context.currentPosition.fileName, context.currentPosition.line))
    }
  }

  def compileError(msg: String) {
    if (!handleError(JbjSettings.ErrorLevel.E_COMPILE_ERROR, msg, context.currentPosition) && !silent &&
      context.settings.getErrorReporting.contains(JbjSettings.ErrorLevel.E_COMPILE_ERROR)) {
      err.foreach(_.println("PHP Compile error: %s in %s on line %d".format(msg, context.currentPosition.fileName, context.currentPosition.line)))
      out.println()
      out.println("Compile error: %s in %s on line %d".format(msg, context.currentPosition.fileName, context.currentPosition.line))
    }
  }

  def warn(msg: String) {
    if (!handleError(JbjSettings.ErrorLevel.E_WARNING, msg, context.currentPosition) && !silent &&
      context.settings.getErrorReporting.contains(JbjSettings.ErrorLevel.E_WARNING)) {
      err.foreach(_.println("PHP Warning: %s in %s on line %d".format(msg, context.currentPosition.fileName, context.currentPosition.line)))
      out.println()
      out.println("Warning: %s in %s on line %d".format(msg, context.currentPosition.fileName, context.currentPosition.line))
    }
  }

  def notice(msg: String) {
    if (!handleError(JbjSettings.ErrorLevel.E_NOTICE, msg, context.currentPosition) && !silent &&
      context.settings.getErrorReporting.contains(JbjSettings.ErrorLevel.E_NOTICE)) {
      err.foreach(_.println("PHP Notice: %s in %s on line %d".format(msg, context.currentPosition.fileName, context.currentPosition.line)))
      out.println()
      out.println("Notice: %s in %s on line %d".format(msg, context.currentPosition.fileName, context.currentPosition.line))
    }

  }

  def strict(msg: String) {
    if (!handleError(JbjSettings.ErrorLevel.E_STRICT, msg, context.currentPosition) && !silent &&
      context.settings.getErrorReporting.contains(JbjSettings.ErrorLevel.E_STRICT)) {
      err.foreach(_.println("PHP Strict Standards: %s in %s on line %d".format(msg, context.currentPosition.fileName, context.currentPosition.line)))
      out.println()
      out.println("Strict Standards: %s in %s on line %d".format(msg, context.currentPosition.fileName, context.currentPosition.line))
    }

  }

  def parseError(position: FileNodePosition, msg: String) {
    if (!silent && context.settings.getErrorReporting.contains(JbjSettings.ErrorLevel.E_PARSE)) {
      err.foreach(_.println("PHP Parse error: %s in %s on line %d".format(msg, position.fileName, position.line)))
      out.println()
      out.println("Parse error: %s in %s on line %d".format(msg, position.fileName, position.line))
    }
  }

  def deprecated(position: NodePosition, msg: String) {
    if (!silent && context.settings.getErrorReporting.contains(JbjSettings.ErrorLevel.E_DEPRECATED)) {
      err.foreach(_.println("PHP Deprecated: %s in %s on line %d".format(msg, position.fileName, position.line)))
      out.println()
      out.println("Deprecated: %s in %s on line %d".format(msg, position.fileName, position.line))
    }
  }

  private def handleError(errorLevel: ErrorLevel, msg: String, position: NodePosition): Boolean = {
    implicit val global = context.global
    global.errorHandler.exists {
      case errorHandler if (context.global.errorHandlerTypes & errorLevel.getValue) != 0 =>
        CallbackHelper.callCallabck(errorHandler, IntegerVal(errorLevel.getValue), StringVal(msg), StringVal(position.fileName), IntegerVal(position.line))
        true
      case _ =>
        false
    }
  }
}
