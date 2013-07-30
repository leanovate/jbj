package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast.{StaticInitializer, Stmt, Expr}
import de.leanovate.jbj.runtime.{ExecResult, SuccessExecResult, Context}
import scala.annotation.tailrec

case class IfStmt(condition: Expr, thenStmts: List[Stmt], elseIfs: List[ElseIfBlock], elseStmts: List[Stmt])
  extends Stmt with StaticInitializer {

  private val staticInitializers =
    thenStmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer]) ++
      elseIfs.map(_.themStmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])).flatten ++
      elseStmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])


  override def exec(ctx: Context) = {
    if (condition.eval(ctx).toBool.value) {
      execStmts(thenStmts, ctx)
    } else {
      elseIfs.find(_.condition.eval(ctx).toBool.value).map {
        elseIf => execStmts(elseIf.themStmts, ctx)
      }.getOrElse {
        execStmts(elseStmts, ctx)
      }
    }
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
