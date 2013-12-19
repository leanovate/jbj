package de.leanovate.jbj.bcmath.functions

import de.leanovate.jbj.runtime.annotations.{ParameterMode, GlobalFunction}
import de.leanovate.jbj.runtime.context.Context
import scala.math.BigDecimal.RoundingMode.FLOOR
import scala.math.BigDecimal.RoundingMode
import scala.annotation.tailrec

object BcMathFunctions {

  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN)
  def bcadd(left: String, right: String, scale: Option[Int])(implicit context: Context): String =
    operation("bcadd", left, right, scale) { _ + _ }

  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN)
  def bcsub(left: String, right: String, scale: Option[Int])(implicit context: Context): String =
    operation("bcsub", left, right, scale) { _ - _ }

  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN)
  def bccomp(left: String, right: String, scale: Option[Int])(implicit context: Context): Int = {
    val s = scale.getOrElse(context.settings.getBcScaleFactor)
    BigDecimal(left).setScale(s, FLOOR).compare(BigDecimal(right).setScale(s, FLOOR))
  }

  @GlobalFunction(parameterMode = ParameterMode.STRICT_WARN)
  def bcdiv(left: String, right: String, scale: Option[Int])(implicit context: Context): String =
    operation("bcdiv", left, right, scale) { _ / _ }

  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN)
  def bcmod(left: String, right: String)(implicit context: Context): String =
    operation("bcmod", left, right, None) { _ % _ }

  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN)
  def bcmul(left: String, right: String, scale: Option[Int])(implicit context: Context): String =
    operation("bcmul", left, right, scale) { _ * _ }

  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN)
  def bcpow(left: String, right: String, scale: Option[Int])(implicit context: Context): String = {
    val s = scale.getOrElse(context.settings.getBcScaleFactor)
    val result = BigDecimal(left).pow(right.toInt)
    result.setScale(s, FLOOR).bigDecimal.stripTrailingZeros().toString()
  }

  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN)
  def bcpowmod(left: String, right: String, modulus: String, scale: Option[Int])(implicit context: Context): String = {
    val l = BigInt(left)
    val m = BigInt(modulus)
    var result = l.modPow(BigInt(right), m)
    if (l < 0) {
      result = result - m
    }
    result.toString()
  }

  @GlobalFunction
  def bcscale(scale: Int)(implicit context: Context): Boolean = {
    if (scale >= 0) {
      context.settings.setBcScaleFactor(scale)
      true
    } else false
  }

  private[this] val SQRT_DIG = BigDecimal(150)
  private[this] val SQRT_PRE = BigDecimal(10).pow(SQRT_DIG.intValue())

  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN)
  def bcsqrt(operand: String, scale: Option[Int])(implicit context: Context): String = {

    @tailrec
    def sqrtNewtonRaphson(c: BigDecimal, xn: BigDecimal, precision: BigDecimal): BigDecimal = {
      val fx = xn.pow(2) - c
      val fpx = xn * BigDecimal(2)
      var xn1 = (fx / fpx).setScale(2 * SQRT_DIG.intValue(), RoundingMode.HALF_DOWN)
      xn1 = xn - xn1
      val currentSquare = xn1.pow(2)
      var currentPrecision = currentSquare - c
      if (currentPrecision < 0)
        currentPrecision = -currentPrecision
      if (currentPrecision < precision) {
        xn1
      } else {
        sqrtNewtonRaphson(c, xn1, precision)
      }
    }

    val s = scale.getOrElse(context.settings.getBcScaleFactor)
    val o = BigDecimal(operand)
    if (o < 0) {
      context.log.warn("bcsqrt(): Square root of negative number")
      ""
    } else {
      val result = if (o.isValidDouble)
        BigDecimal(Math.sqrt(o.toDouble))
      else
        sqrtNewtonRaphson(o, BigDecimal(1), BigDecimal(1) / SQRT_PRE)
      result.setScale(s, FLOOR).toString()
    }
  }

  private[this] def operation(functionName: String, left: String, right: String, scale: Option[Int])(f: (BigDecimal, BigDecimal) => BigDecimal)(implicit c: Context) = {
    val s = scale.getOrElse(c.settings.getBcScaleFactor)
    try {
      val result = f(BigDecimal(left), BigDecimal(right))
      result.setScale(s, FLOOR).toString()
    } catch {
      case a: java.lang.ArithmeticException => c.log.warn(functionName + "(): " + a.getMessage); ""
    }

  }
}
