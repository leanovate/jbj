package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{FilePosition, Stmt, Expr}
import de.leanovate.jbj.runtime.{ReturnExecResult, Context}
import de.leanovate.jbj.runtime.value.UndefinedVal

case class ReturnStmt(position: FilePosition, expr: Option[Expr]) extends Stmt {
  def exec(ctx: Context) = {
    ReturnExecResult(expr.map(_.eval(ctx)).getOrElse(UndefinedVal))
  }
}
