/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt.cond

import de.leanovate.jbj.core.ast._
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.runtime.context.{Context, StaticContext}
import de.leanovate.jbj.runtime.BreakExecResult

case class SwitchStmt(expr: Expr, cases: List[SwitchCase]) extends Stmt with StaticInitializer with BlockLike {
  private lazy val defaultCases: List[SwitchCase] = {
    var lastDefault = -1
    cases.zipWithIndex.foreach {
      case (switchCase, idx) if switchCase.isDefault =>
        lastDefault = idx
      case _ =>
    }
    if (lastDefault < 0)
      Nil
    else
      cases.drop(lastDefault)
  }

  private val staticInitializers =
    cases.map(_.stmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])).flatten

  override def exec(implicit ctx: Context) = {
    val value = expr.eval.asVal

    var matching = cases.dropWhile(!_.matches(value))
    if (matching.isEmpty) {
      matching = defaultCases
    }
    execStmts(matching.map(_.stmts).flatten) match {
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