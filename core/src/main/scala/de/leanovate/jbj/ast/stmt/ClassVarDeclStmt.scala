package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Stmt, MemberModifier, FilePosition}
import de.leanovate.jbj.runtime.Context

case class ClassVarDeclStmt(position: FilePosition, modifieres: Set[MemberModifier.Type],
                            assignments: List[StaticAssignment]) extends Stmt {
  def exec(ctx: Context) = ???
}
