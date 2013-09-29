/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.buildins.functions

import de.leanovate.jbj.runtime.value.{NullVal, PVal}
import de.leanovate.jbj.runtime.annotations.GlobalFunction
import de.leanovate.jbj.runtime.context.Context
import java.util
import scala.collection.JavaConversions._
import de.leanovate.jbj.runtime.{NamespaceName, CallbackHelper}
import de.leanovate.jbj.api.http.JbjSettings

object RuntimeFunctions {

  @GlobalFunction
  def error_reporting(value: Int)(implicit ctx: Context) {
    val errorReporing = JbjSettings.ErrorLevel.values().foldLeft(util.EnumSet.noneOf(classOf[JbjSettings.ErrorLevel])) {
      (set, enum) => if ((enum.getValue & value) != 0) {
        set.add(enum)
      }
        set
    }
    ctx.settings.setErrorReporting(errorReporing)
  }

  @GlobalFunction
  def set_error_handler(value: PVal, errorTypes: Option[Int])(implicit ctx: Context) {
    if (CallbackHelper.isValidCallback(value)) {
      ctx.global.errorHandlerTypes = errorTypes.getOrElse {
        JbjSettings.E_ALL.foldLeft(0) {
          (r, errorLevel) => r | errorLevel.getValue
        }
      }
      ctx.global.errorHandler = Some(value)
    } else {
      ctx.log.warn("set_error_handler() expects the argument (%s) to be a valid callback".format(value.toStr.asString))
    }
  }

  @GlobalFunction
  def register_shutdown_function(optCallback: Option[PVal], parameters: PVal*)(implicit ctx: Context) {
    optCallback match {
      case Some(callback) if !CallbackHelper.isValidCallback(callback) =>
        ctx.log.warn("register_shutdown_function(): Invalid shutdown callback '%s' passed".format(callback.toStr.asString))
      case Some(callback) =>
        ctx.global.shutdownHandler = Some(callback)
        ctx.global.shutdownParameters = parameters
      case None =>
        ctx.log.warn("Wrong parameter count for register_shutdown_function()")
    }
  }

  @GlobalFunction
  def define(name: String, value: PVal, caseInsensitive: Option[Boolean])(implicit ctx: Context) {
    ctx.global.defineConstant(NamespaceName(name), value, caseInsensitive.getOrElse(false))
  }

  @GlobalFunction
  def defined(name: String)(implicit ctx: Context): Boolean = ctx.global.findConstant(NamespaceName(name)).isDefined

  @GlobalFunction
  def constant(name: String)(implicit ctx: Context): PVal = ctx.global.findConstant(NamespaceName(name)).getOrElse {
    ctx.log.warn("constant(): Couldn't find constant %s".format(name))
    NullVal
  }

  @GlobalFunction
  def ini_set(name: String, value: PVal)(implicit ctx: Context) {
    name.toLowerCase match {
      case "error_reporting" =>
        ctx.settings.setErrorReporting(JbjSettings.ErrorLevel.errorLevelsForValue(value.toInteger.asInt))
    }
  }

  @GlobalFunction
  def trigger_error(error_msg: String, error_type: Option[Int])(implicit ctx: Context): Boolean = {
    val errorLevel = JbjSettings.ErrorLevel.errorLevelForValue(error_type.getOrElse(JbjSettings.ErrorLevel.E_USER_NOTICE.getValue))

    Option(errorLevel) match {
      case Some(JbjSettings.ErrorLevel.E_USER_NOTICE) =>
        ctx.log.userNotice(error_msg)
        true
      case Some(JbjSettings.ErrorLevel.E_USER_WARNING) =>
        ctx.log.userWarn(error_msg)
        true
      case Some(JbjSettings.ErrorLevel.E_USER_ERROR) =>
        ctx.log.userError(error_msg)
        true
      case _ =>
        false
    }
  }
}
