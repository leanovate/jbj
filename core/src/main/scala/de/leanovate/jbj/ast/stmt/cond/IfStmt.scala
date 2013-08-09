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


  override def exec(implicit ctx: Context) = {
    if (condition.eval.toBool.asBoolean) {
      execStmts(thenStmts)
    } else {
      elseIfs.find(_.condition.eval.toBool.asBoolean).map {
        elseIf => execStmts(elseIf.themStmts)
      }.getOrElse {
        execStmts(elseStmts)
      }
    }
  }

  override def initializeStatic(implicit ctx: Context) {
    staticInitializers.foreach(_.initializeStatic)
  }

  @tailrec
  private def execStmts(statements: List[Stmt])(implicit context: Context): ExecResult = statements match {
    case head :: tail => head.exec match {
      case SuccessExecResult => execStmts(tail)
      case result => result
    }
    case Nil => SuccessExecResult
  }
}
