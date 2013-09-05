/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.runtime.adapter

import de.leanovate.jbj.core.runtime.value.PVal
import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.context.Context

object PValConverter extends Converter[PVal, PVal] {
  override def toScalaWithConversion(expr: Expr)(implicit ctx: Context) = expr.eval.asVal

  override def toScala(value: PVal)(implicit ctx: Context) = value

  override def toJbj(value: PVal)(implicit ctx: Context) = value
}
