package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{NodePosition, Stmt, Expr}
import de.leanovate.jbj.runtime.{ReturnExecResult, Context}
import de.leanovate.jbj.runtime.value.UndefinedVal

case class ReturnStmt(expr: Option[Expr]) extends Stmt {
  def exec(ctx: Context) = {
    ReturnExecResult(expr.map(_.eval(ctx)).getOrElse(UndefinedVal))
  }
}
