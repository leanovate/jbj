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

case class StdParamterAdapter[T, S <: PAny](parameterIdx:Int, converter: Converter[T, S]) extends ParameterAdapter[T] {
  override def requiredCount = 1

  override def adapt(parameters: List[PParam], strict: Boolean, missingErrorHandler: => Unit, conversionErrorHandler: (String, String) => Unit)(implicit ctx: Context) = {
    if (strict) {
      parameters match {
        case head :: tail =>
          converter.toScala(head) match {
            case Some(v) => (v, tail)
            case None =>
              conversionErrorHandler(converter.typeName, head.byVal.typeName(simple = false))
              (converter.missingValue, tail)
          }
        case Nil =>
          missingErrorHandler
          (converter.missingValue, Nil)
      }
    } else {
      parameters match {
        case head :: tail => (converter.toScalaWithConversion(head), tail)
        case Nil =>
          missingErrorHandler
          (converter.missingValue, Nil)
      }
    }
  }
}
