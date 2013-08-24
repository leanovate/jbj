package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.Stmt
import de.leanovate.jbj.core.runtime.SuccessExecResult
import de.leanovate.jbj.core.runtime.context.Context

case class LabelStmt(label: String) extends Stmt {
  override def exec(implicit ctx: Context) = SuccessExecResult
}
