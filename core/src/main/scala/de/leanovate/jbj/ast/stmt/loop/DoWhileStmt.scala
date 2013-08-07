package de.leanovate.jbj.ast.stmt.loop

import de.leanovate.jbj.ast.{StaticInitializer, Stmt, Expr}
import de.leanovate.jbj.runtime._
import scala.annotation.tailrec
import de.leanovate.jbj.runtime.BreakExecResult
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.ast.stmt.BlockLike

case class DoWhileStmt(stmts: List[Stmt], condition: Expr) extends Stmt with BlockLike with StaticInitializer {
  private val staticInitializers = stmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])

  override def exec(implicit ctx: Context): ExecResult = {
    do {
      execStmts(stmts) match {
        case BreakExecResult(depth) if depth > 1 => BreakExecResult(depth - 1)
        case BreakExecResult(_) => return SuccessExecResult
        case result: ReturnExecResult => return result
        case _ =>
      }
    } while (condition.eval.toBool.value)
    SuccessExecResult
  }

  override def initializeStatic(implicit ctx: Context) {
    staticInitializers.foreach(_.initializeStatic)
  }
}
