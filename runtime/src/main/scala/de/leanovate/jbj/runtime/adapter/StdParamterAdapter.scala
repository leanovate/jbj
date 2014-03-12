/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.PAny
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.PParam

case class StdParamterAdapter[T, S <: PAny](parameterIdx: Int,
                                            converter: Converter[T, S],
                                            strict: Boolean,
                                            errorHandlers: ParameterAdapter.ErrorHandlers)
  extends ParameterAdapter[T] {
  override def requiredCount = 1

  override def adapt(parameters: List[PParam])(implicit ctx: Context) = {
    if (strict) {
      parameters match {
        case head :: tail =>
          converter.toScala(head) match {
            case Some(v) => (v, tail)
            case None =>
              errorHandlers.conversionError(converter.typeName, head.byVal.typeName(simple = false), parameterIdx)
              (converter.missingValue, tail)
          }
        case Nil =>
          errorHandlers.parameterMissing(parameterIdx)
          (converter.missingValue, Nil)
      }
    } else {
      parameters match {
        case head :: tail => (converter.toScalaWithConversion(head), tail)
        case Nil =>
          errorHandlers.parameterMissing(parameterIdx)
          (converter.missingValue, Nil)
      }
    }
  }
}
