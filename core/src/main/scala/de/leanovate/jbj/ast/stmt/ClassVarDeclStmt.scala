package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Stmt, MemberModifier}
import de.leanovate.jbj.runtime.{SuccessExecResult, Context}
import de.leanovate.jbj.runtime.value.{ValueRef, NullVal}

case class ClassVarDeclStmt(modifieres: Set[MemberModifier.Type],
                            assignments: List[StaticAssignment]) extends Stmt {
  override def exec(implicit ctx: Context) = {
    assignments.foreach {
      assignment =>

        ctx.defineVariable(assignment.variableName, ValueRef(assignment.initial.map(_.eval)))
    }
    SuccessExecResult
  }

}
