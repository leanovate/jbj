/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt.cond

import de.leanovate.jbj.core.ast._
import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.core.ast.stmt.BlockLike

case class IfStmt(condition: Expr, thenStmts: List[Stmt], elseIfs: List[ElseIfBlock], elseStmts: List[Stmt])
  extends Stmt  with BlockLike {

  override def exec(implicit ctx: Context) = {
    if (condition.eval.asVal.toBool.asBoolean) {
      execStmts(thenStmts)
    } else {
      elseIfs.find(_.condition.eval.asVal.toBool.asBoolean).map {
        elseIf => execStmts(elseIf.themStmts)
      }.getOrElse {
        execStmts(elseStmts)
      }
    }
  }

  override def visit[R](visitor: NodeVisitor[R]) =
    visitor(this).thenChild(condition).thenChildren(thenStmts).thenChildren(elseIfs).thenChildren(elseStmts)
}
