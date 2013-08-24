package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.Stmt
import de.leanovate.jbj.core.runtime.context.Context

case class ConstDeclStmt(assignments: List[StaticAssignment]) extends Stmt{
  override def exec(implicit ctx: Context) = ???
}
