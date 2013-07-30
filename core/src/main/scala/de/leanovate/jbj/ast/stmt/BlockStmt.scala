package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{StaticInitializer, NodePosition, Stmt}
import de.leanovate.jbj.runtime._
import scala.annotation.tailrec
import de.leanovate.jbj.runtime.SuccessExecResult

case class BlockStmt( stmts: List[Stmt]) extends Stmt with StaticInitializer {
  private val staticInitializers = stmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])

  override def exec(ctx: Context) = {
    execStmts(stmts, ctx)
  }

  override def initializeStatic(ctx: Context) {
    staticInitializers.foreach(_.initializeStatic(ctx))
  }

  @tailrec
  private def execStmts(statements: List[Stmt], context: Context): ExecResult = statements match {
    case head :: tail => head.exec(context) match {
      case SuccessExecResult() => execStmts(tail, context)
      case result => result
    }
    case Nil => SuccessExecResult()
  }

}