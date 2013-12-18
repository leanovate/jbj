package de.leanovate.jbj.bcmath.functions

import de.leanovate.jbj.runtime.annotations.{ParameterMode, GlobalFunction}
import de.leanovate.jbj.runtime.context.Context
import scala.math.BigDecimal.RoundingMode.FLOOR

object BcMathFunctions {

  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN)
  def bcadd(left: String, right: String, scale: Option[Int])(implicit context: Context): String = {
    val s = scale.getOrElse(context.settings.getBcScaleFactor)
    val result = BigDecimal(left) + BigDecimal(right)
    result.setScale(s, FLOOR).toString()
  }

  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN)
  def bccomp(left: String, right: String, scale: Option[Int])(implicit context: Context): Int = {
    val s = scale.getOrElse(context.settings.getBcScaleFactor)
    BigDecimal(left).setScale(s, FLOOR).compare(BigDecimal(right).setScale(s, FLOOR))
  }

  private def arithmeticExceptionHandler(functionName: String)(implicit context: Context): PartialFunction[Throwable, String] = {
    case a: java.lang.ArithmeticException => context.log.warn(functionName + "(): " + a.getMessage); ""
  }

  @GlobalFunction(parameterMode = ParameterMode.STRICT_WARN)
  def bcdiv(left: String, right: String, scale: Option[Int])(implicit context: Context): String = {
    val s = scale.getOrElse(context.settings.getBcScaleFactor)
    try {
      val result = BigDecimal(left) / BigDecimal(right)
      result.setScale(s, FLOOR).toString()
    } catch arithmeticExceptionHandler("bcdiv")
  }

  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN)
  def bcmod(left: String, right: String)(implicit context: Context): String = {
    try {
      val result = BigDecimal(left) % BigDecimal(right)
      result.toString()
    } catch arithmeticExceptionHandler("bcmod")
  }

}
