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

case class RelaxedParamterAdapter[T, S <: PAny](parameterIdx: Int,
                                                converter: Converter[T, S],
                                                errorHandlers: ParameterAdapter.ErrorHandlers)
  extends ParameterAdapter[T] {
  override def requiredCount = 1

  override def adapt(parameters: Iterator[PParam])(implicit ctx: Context) = {
    if (parameters.hasNext) {
      converter.toScalaWithConversion(parameters.next())
    } else {
      errorHandlers.parameterMissing(parameterIdx)
      converter.missingValue
    }
  }
}
