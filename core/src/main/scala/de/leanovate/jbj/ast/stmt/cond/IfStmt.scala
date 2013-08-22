package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast._
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.ast.stmt.BlockLike

case class IfStmt(condition: Expr, thenStmts: List[Stmt], elseIfs: List[ElseIfBlock], elseStmts: List[Stmt])
  extends Stmt  with BlockLike {

  override def exec(implicit ctx: Context) = {
    if (condition.evalOld.toBool.asBoolean) {
      execStmts(thenStmts)
    } else {
      elseIfs.find(_.condition.evalOld.toBool.asBoolean).map {
        elseIf => execStmts(elseIf.themStmts)
      }.getOrElse {
        execStmts(elseStmts)
      }
    }
  }

  override def visit[R](visitor: NodeVisitor[R]) =
    visitor(this).thenChild(condition).thenChildren(thenStmts).thenChildren(elseIfs).thenChildren(elseStmts)
}
