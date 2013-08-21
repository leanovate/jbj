package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.Stmt
import de.leanovate.jbj.runtime.context.Context

case class ConstDeclStmt(assignments: List[StaticAssignment]) extends Stmt{
  override def exec(implicit ctx: Context) = ???
}
