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

  def adapt(parameters: Iterator[PParam])(implicit ctx: Context): T
}

object ParameterAdapter {

  class ErrorHandlers(tooManyErrorHandler: (Int) => Unit,
                      missingErrorHandler: (Int, Context) => Unit,
                      conversionErrorHandler: (String, String, Int) => Unit) {
    def tooManyParameters(actual: Int) = tooManyErrorHandler(actual)

    def parameterMissing(actual: Int)(implicit ctx: Context) = missingErrorHandler(actual, ctx)

    def conversionError(expectedTypeName: String, givenTypeName: String, parameterIdx: Int) =
      conversionErrorHandler(expectedTypeName, givenTypeName, parameterIdx)
  }

  def errorHandlers(methodName: String, parameterMode: ParameterMode.Type, expectedMin: Int, expectedMax:Int, hasOptional: Boolean, warnResult: PVal) = {
    parameterMode match {
      case ParameterMode.EXACTLY_WARN if hasOptional =>
        new ErrorHandlers(
          tooManyWarn(methodName, expectedMax, warnResult),
          notEnoughWarn(methodName, expectedMin, warnResult),
          conversionErrorIgnore)
      case ParameterMode.EXACTLY_WARN =>
        new ErrorHandlers(
          tooManyExactlyWarn(methodName, expectedMax, warnResult),
          notEnoughExactlyWarn(methodName, expectedMin, warnResult),
          conversionErrorIgnore)
      case ParameterMode.STRICT_WARN if hasOptional =>
        new ErrorHandlers(
          tooManyWarn(methodName, expectedMax, warnResult),
          notEnoughWarn(methodName, expectedMin, warnResult),
          conversionErrorWarn(methodName, warnResult))
      case ParameterMode.STRICT_WARN =>
        new ErrorHandlers(
          tooManyExactlyWarn(methodName, expectedMax, warnResult),
          notEnoughExactlyWarn(methodName, expectedMin, warnResult),
          conversionErrorWarn(methodName, warnResult))
      case ParameterMode.RELAX_ERROR =>
        new ErrorHandlers(
          tooManyIgnore,
          notEnoughThrowFatal(methodName, expectedMin),
          conversionErrorIgnore)
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

  def notEnoughWarn(name: String, expectedMin: Int, result: PVal): (Int, Context) => Unit = {
    (actual: Int, ctx: Context) =>
      val msg = s"$name() expects at least ${plural(expectedMin, "parameter")}, $actual given"
      throw new WarnWithResultJbjException(msg, result)
  }

  def notEnoughExactlyWarn(name: String, expectedMin: Int, result: PVal): (Int, Context) => Unit = {
    (actual: Int, ctx: Context) =>
      val msg = s"$name() expects exactly ${plural(expectedMin, "parameter")}, $actual given"
      throw new WarnWithResultJbjException(msg, result)
  }

  def notEnoughThrowFatal(name: String, expectedMin: Int): (Int, Context) => Unit = {
    (actual: Int, ctx: Context) =>
      val msg = s"$name() expects at least ${plural(expectedMin, "parameter")}, $actual given"
      throw new FatalErrorJbjException(msg)(ctx)
  }

  val tooManyIgnore = {
    (actual: Int) =>
  }

  def tooManyWarn(name: String, expectedMax: Int, result: PVal): (Int) => Unit = {
    (actual: Int) =>
      val msg = s"$name() expects at most ${plural(expectedMax, "parameter")}, $actual given"
      throw new WarnWithResultJbjException(msg, result)
  }

  def tooManyExactlyWarn(name: String, expectedMax: Int, result: PVal): (Int) => Unit = {
    (actual: Int) =>
      val msg = s"$name() expects exactly ${plural(expectedMax, "parameter")}, $actual given"
      throw new WarnWithResultJbjException(msg, result)
  }

  def plural(num: Int, str: String) = {
    if (num == 0 || num > 1)
      s"$num ${str}s"
    else
      s"$num $str"
  }
}