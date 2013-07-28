package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Modifier, FilePosition, Stmt}
import de.leanovate.jbj.runtime.{SuccessExecResult, Context}
import de.leanovate.jbj.runtime.value.UndefinedVal

case class AssignStmt(position: FilePosition, modifiers: Set[Modifier.Type], assignments: List[Assignment]) extends Stmt {
  override def exec(ctx: Context) = {
    assignments.foreach {
      assignment =>
        val value = assignment.expr.map(_.eval(ctx)).getOrElse(UndefinedVal)
        assignment.reference.assign(ctx, value)
    }
    SuccessExecResult()
  }
}
