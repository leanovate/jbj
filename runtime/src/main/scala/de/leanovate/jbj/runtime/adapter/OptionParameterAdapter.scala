/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.PAny
import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.PParam

case class OptionParameterAdapter[T, S <: PAny](converter: Converter[T, S]) extends ParameterAdapter[Option[T]] {
  override def requiredCount = 0

  override def adapt(parameters: List[PParam])(implicit ctx: Context) =
    parameters match {
      case head :: tail => Some(Some(converter.toScalaWithConversion(head)), tail)
      case Nil => Some(None, Nil)
    }
}
