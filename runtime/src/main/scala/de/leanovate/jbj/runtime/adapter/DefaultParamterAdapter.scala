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

case class DefaultParamterAdapter[T, S <: PAny](converter: Converter[T, S]) extends ParameterAdapter[T] {
  override def requiredCount = 1

  override def adapt(parameters: List[PParam])(implicit ctx: Context) =
    parameters match {
      case head :: tail => Some(converter.toScalaWithConversion(head), tail)
      case Nil => None
    }
}
