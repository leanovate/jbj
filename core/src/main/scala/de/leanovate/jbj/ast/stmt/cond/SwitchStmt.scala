package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast._
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.runtime.context.{Context, StaticContext}
import de.leanovate.jbj.ast.stmt.BlockLike
import de.leanovate.jbj.runtime.BreakExecResult

case class SwitchStmt(expr: Expr, cases: List[SwitchCase]) extends Stmt with StaticInitializer with BlockLike {
  private val staticInitializers =
    cases.map(_.stmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])).flatten

  override def exec(implicit ctx: Context) = {
    val value = expr.eval.asVal

    execStmts(cases.dropWhile(!_.matches(value)).map(_.stmts).flatten) match {
      case BreakExecResult(depth) if depth > 1 => BreakExecResult(depth - 1)
      case BreakExecResult(_) => SuccessExecResult
      case result => result
    }
  }

  override def initializeStatic(staticCtx: StaticContext)(implicit ctx: Context) {
    staticInitializers.foreach(_.initializeStatic(staticCtx))
  }

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(cases)
}