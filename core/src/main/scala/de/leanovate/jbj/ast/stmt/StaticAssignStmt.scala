package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Modifier, FilePosition, Stmt}
import de.leanovate.jbj.runtime.{ValueRef, SuccessExecResult, Context}
import de.leanovate.jbj.runtime.value.{UndefinedVal}

case class StaticAssignStmt(position: FilePosition, modifiers: Set[Modifier.Type], assignments: List[Assignment]) extends Stmt {
  override def exec(ctx: Context) = {
    assignments.foreach {
      assignment =>
        val value = assignment.expr.map(_.eval(ctx)).getOrElse(UndefinedVal)
        assignment.reference.assignInitial(ctx.static, value)
    }
    SuccessExecResult()
  }
}