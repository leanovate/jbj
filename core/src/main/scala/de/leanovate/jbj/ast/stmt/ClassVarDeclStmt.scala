package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Stmt, MemberModifier, NodePosition}
import de.leanovate.jbj.runtime.Context

case class ClassVarDeclStmt( modifieres: Set[MemberModifier.Type],
                            assignments: List[StaticAssignment]) extends Stmt {
  def exec(ctx: Context) = ???
}
