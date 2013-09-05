/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.runtime.adapter

import de.leanovate.jbj.core.runtime.value.StringVal
import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.context.Context

object ByteArrayConverter extends Converter[Array[Byte], StringVal] {
  def toScalaWithConversion(expr: Expr)(implicit ctx: Context) = expr.eval.asVal.toStr.chars

  def toScala(value: StringVal)(implicit ctx: Context) = value.chars

  def toJbj(value: Array[Byte])(implicit ctx: Context) = StringVal(value)
}
