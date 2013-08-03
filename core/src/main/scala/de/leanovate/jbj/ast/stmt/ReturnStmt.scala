package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Stmt, Expr}
import de.leanovate.jbj.runtime.{ReturnExecResult, Context}
import de.leanovate.jbj.runtime.value.UndefinedVal

case class ReturnStmt(expr: Option[Expr]) extends Stmt {
  override def exec(implicit ctx: Context) = {
    ReturnExecResult(expr.map(_.eval).getOrElse(UndefinedVal))
  }
}
