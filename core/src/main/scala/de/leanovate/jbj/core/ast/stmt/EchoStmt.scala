/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.{NodeVisitor, Expr, Stmt}
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.runtime.context.Context

case class EchoStmt(parameters: Seq[Expr]) extends Stmt {
  override def exec(implicit ctx: Context) = {
    parameters.foreach {
      expr =>
        ctx.out.print(expr.eval.toOutput)
    }
    SuccessExecResult
  }

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(parameters)
}
