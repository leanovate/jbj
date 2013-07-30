package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.Stmt
import de.leanovate.jbj.runtime.{SuccessExecResult, Context}

case class LabelStmt(label: String) extends Stmt {
  def exec(ctx: Context) = SuccessExecResult()
}
