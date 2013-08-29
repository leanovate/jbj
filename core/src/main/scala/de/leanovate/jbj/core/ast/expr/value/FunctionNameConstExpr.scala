/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr.value

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.context.{Context, FunctionContext, MethodContext}
import de.leanovate.jbj.core.runtime.value.StringVal

case class FunctionNameConstExpr() extends Expr {
  def eval(implicit ctx: Context) = ctx match {
    case MethodContext(_, pMethod, _) => StringVal(pMethod.name)
    case FunctionContext(name, _) => StringVal(name.toString)
    case _ => StringVal("")
  }
}
