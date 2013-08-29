/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.runtime.adapter

import de.leanovate.jbj.core.runtime.value.PAny
import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.context.Context

case class VarargParameterAdapter[T, S <: PAny](converter: Converter[T, S]) extends ParameterAdapter[Seq[T]] {
  override def requiredCount = 0

  override def adapt(parameters: List[Expr])(implicit ctx: Context) =
    Some(parameters.map {
      parameter =>
        converter.toScalaWithConversion(parameter)
    }, Nil)
}