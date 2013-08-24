package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.{ReferableExpr, Stmt}
import de.leanovate.jbj.core.runtime.SuccessExecResult
import de.leanovate.jbj.core.runtime.context.Context

case class UnsetStmt(references: List[ReferableExpr]) extends Stmt {
  def exec(implicit ctx: Context) = {
    references.foreach(_.evalRef.unset())
    SuccessExecResult
  }
}
