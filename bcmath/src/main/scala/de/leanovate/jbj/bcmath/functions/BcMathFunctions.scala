package de.leanovate.jbj.bcmath.functions

import de.leanovate.jbj.runtime.annotations.{ParameterMode, GlobalFunction}
import de.leanovate.jbj.runtime.context.Context
import scala.math.BigDecimal.RoundingMode

object BcMathFunctions {
  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN)
  def bcadd(left: String, right: String, scale: Option[Int])(implicit context: Context): String = {
    val bcScale = scale.getOrElse(context.settings.getBcScaleFactor)
    val result = BigDecimal(left) + BigDecimal(right)
    result.setScale(bcScale, RoundingMode.FLOOR).toString()
  }
}
