/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.Stmt
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.{NamespaceName, SuccessExecResult}
import de.leanovate.jbj.runtime.value.{ArrayVal, LazyVal}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.core.ast.expr.ArrayCreateExpr

case class ConstDeclStmt(assignments: List[StaticAssignment]) extends Stmt{
  override def exec(implicit ctx: Context) = {
    assignments.foreach {
      case StaticAssignment(name, Some(expr)) =>
        expr match {
          case _: ArrayCreateExpr =>
            throw new FatalErrorJbjException("Arrays are not allowed as constants")
          case _ =>
            ctx.global.defineConstant(NamespaceName(name).absolutePrefix, new LazyVal {
              def value = expr.eval.concrete
            }, caseInsensitive = false)
        }
      case _ =>
    }
    SuccessExecResult
  }
}
