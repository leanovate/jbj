/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.runtime.buildin

import de.leanovate.jbj.core.runtime.value.PVal
import de.leanovate.jbj.core.runtime.annotations.GlobalFunction
import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.api.JbjSettings
import java.util
import scala.collection.JavaConversions._

object RuntimeFunctions extends WrappedFunctions {

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
  def define(name: String, value: PVal, caseInsensitive: Option[Boolean])(implicit ctx: Context) {
    ctx.global.defineConstant(name, value, caseInsensitive.getOrElse(false))
  }

  def main(args: Array[String]) {
    println(functions)
  }
}
