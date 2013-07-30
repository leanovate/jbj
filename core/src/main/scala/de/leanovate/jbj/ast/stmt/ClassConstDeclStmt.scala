package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{FilePosition, Stmt}
import de.leanovate.jbj.runtime.Context

case class ClassConstDeclStmt(position: FilePosition, assignments: List[StaticAssignment]) extends Stmt {
  def exec(ctx: Context) = ???
}
