package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{ReferableExpr, Stmt}
import de.leanovate.jbj.runtime.{SuccessExecResult}
import de.leanovate.jbj.runtime.context.Context

case class UnsetStmt(references: List[ReferableExpr]) extends Stmt {
  def exec(implicit ctx: Context) = {
    references.foreach(_.evalRef.unset())
    SuccessExecResult
  }
}
