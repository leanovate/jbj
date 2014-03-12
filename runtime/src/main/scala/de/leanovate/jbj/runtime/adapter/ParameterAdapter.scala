/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.PParam
import de.leanovate.jbj.runtime.value.PVal
import de.leanovate.jbj.runtime.exception.{FatalErrorJbjException, WarnWithResultJbjException}
import de.leanovate.jbj.runtime.annotations.ParameterMode

trait ParameterAdapter[T] {
  def parameterIdx: Int

  def requiredCount: Int

  def adapt(parameters: List[PParam])(implicit ctx: Context): (T, List[PParam])
}

object ParameterAdapter {

  class ErrorHandlers(missingErrorHandler: (Int, Context) => Unit,
                      conversionErrorHandler: (String, String, Int) => Unit) {
    def parameterMissing(actual: Int)(implicit ctx: Context) = missingErrorHandler(actual, ctx)

    def conversionError(expectedTypeName: String, givenTypeName: String, parameterIdx: Int) =
      conversionErrorHandler(expectedTypeName, givenTypeName, parameterIdx)
  }

  def errorHandlers(methodName: String, parameterMode: ParameterMode.Type, expected: Int, hasOptional: Boolean, warnResult: PVal) = {
    parameterMode match {
      case ParameterMode.EXACTLY_WARN if hasOptional =>
        new ErrorHandlers(notEnoughWarn(methodName, expected, warnResult), conversionErrorIgnore)
      case ParameterMode.EXACTLY_WARN =>
        new ErrorHandlers(notEnoughExactlyWarn(methodName, expected, warnResult), conversionErrorIgnore)
      case ParameterMode.STRICT_WARN if hasOptional =>
        new ErrorHandlers(notEnoughWarn(methodName, expected, warnResult),
          conversionErrorWarn(methodName, warnResult))
      case ParameterMode.STRICT_WARN =>
        new ErrorHandlers(notEnoughExactlyWarn(methodName, expected, warnResult),
          conversionErrorWarn(methodName, warnResult))
      case ParameterMode.RELAX_ERROR =>
        new ErrorHandlers(notEnoughThrowFatal(methodName, expected), conversionErrorIgnore)
    }
  }

  val conversionErrorIgnore = {
    (expectedTypeName: String, givenTypeName: String, parameterIdx: Int) =>
  }

  def conversionErrorWarn(name: String, result: PVal) = {
    (expectedTypeName: String, givenTypeName: String, parameterIdx: Int) =>
      val msg = s"$name() expects parameter ${parameterIdx + 1} to be $expectedTypeName, $givenTypeName given"
      throw new WarnWithResultJbjException(msg, result)
  }

  def notEnoughWarn(name: String, expected: Int, result: PVal): (Int, Context) => Unit = {
    (actual: Int, ctx: Context) =>
      val msg = s"$name() expects at least ${plural(expected, "parameter")}, $actual given"
      throw new WarnWithResultJbjException(msg, result)
  }

  def notEnoughExactlyWarn(name: String, expected: Int, result: PVal): (Int, Context) => Unit = {
    (actual: Int, ctx: Context) =>
      val msg = s"$name() expects exactly ${plural(expected, "parameter")}, $actual given"
      throw new WarnWithResultJbjException(msg, result)
  }

  def notEnoughThrowFatal(name: String, expected: Int): (Int, Context) => Unit = {
    (actual: Int, ctx: Context) =>
      val msg = s"$name() expects at least ${plural(expected, "parameter")}, $actual given"
      throw new FatalErrorJbjException(msg)(ctx)
  }

  def plural(num: Int, str: String) = {
    if (num == 0 || num > 1)
      s"$num ${str}s"
    else
      s"$num $str"
  }
}