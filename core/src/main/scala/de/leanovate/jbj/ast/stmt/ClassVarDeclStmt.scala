package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Stmt, MemberModifier}
import de.leanovate.jbj.runtime.{SuccessExecResult, ValueRef, Context}

case class ClassVarDeclStmt(modifieres: Set[MemberModifier.Type],
                            assignments: List[StaticAssignment]) extends Stmt {
  override def exec(ctx: Context) = {
    assignments.foreach {
      assignment =>

        ctx.defineVariable(assignment.variableName, ValueRef(assignment.initial))
    }
    SuccessExecResult()
  }

}
