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

object PAnyConverter extends Converter[PAny, PAny] {
  override def toScalaWithConversion(expr: Expr)(implicit ctx: Context) =  expr.eval.asVal

  override def toScala(value: PAny)(implicit ctx: Context) = value

  override def toJbj(valueOrRef: PAny)(implicit ctx: Context) = valueOrRef
}
