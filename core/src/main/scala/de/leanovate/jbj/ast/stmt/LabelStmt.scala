package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Stmt, FilePosition}
import de.leanovate.jbj.runtime.{SuccessExecResult, Context}

case class LabelStmt(position: FilePosition, label: String) extends Stmt {
  def exec(ctx: Context) = SuccessExecResult()
}
