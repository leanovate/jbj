package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{MemberModifier, Stmt}
import de.leanovate.jbj.runtime.Context

case class ClassMethodDeclStmt(modifieres: Set[MemberModifier.Type], name: String, parameters: List[ParameterDecl], body: List[Stmt]) extends Stmt {
  def exec(ctx: Context) = ???
}
