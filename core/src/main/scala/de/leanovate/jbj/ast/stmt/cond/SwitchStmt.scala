package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast.{StaticInitializer, NodePosition, Expr, Stmt}
import de.leanovate.jbj.runtime._
import scala.annotation.tailrec
import de.leanovate.jbj.runtime.SuccessExecResult

case class SwitchStmt( expr: Expr, cases: List[SwitchCase]) extends Stmt with StaticInitializer {
  private val staticInitializers =
    cases.map(_.stmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])).flatten

  override def exec(ctx: Context) = {
    val value = expr.eval(ctx)

    execStmts(cases.dropWhile(!_.matches(value, ctx)).map(_.stmts).flatten, ctx)
  }

  override def initializeStatic(ctx: Context) {
    staticInitializers.foreach(_.initializeStatic(ctx))
  }

  @tailrec
  private def execStmts(statements: List[Stmt], context: Context): ExecResult = statements match {
    case head :: tail => head.exec(context) match {
      case SuccessExecResult() => execStmts(tail, context)
      case BreakExecResult(depth) if depth > 1 => BreakExecResult(depth - 1)
      case BreakExecResult(_) => SuccessExecResult()
      case result => result
    }
    case Nil => SuccessExecResult()
  }
}