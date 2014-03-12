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

case class RelaxedOptionParameterAdapter[T, S <: PAny](parameterIdx: Int,
                                                       converter: Converter[T, S])
  extends ParameterAdapter[Option[T]] {
  override def requiredCount = 0

  override def adapt(parameters: Iterator[PParam])(implicit ctx: Context) = {
    if (parameters.hasNext) {
      Some(converter.toScalaWithConversion(parameters.next()))
    } else {
      None
    }
  }
}
