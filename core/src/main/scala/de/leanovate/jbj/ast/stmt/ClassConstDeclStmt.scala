package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.Stmt
import de.leanovate.jbj.runtime.Context

case class ClassConstDeclStmt(assignments: List[StaticAssignment]) extends Stmt {
  def exec(ctx: Context) = ???
}
