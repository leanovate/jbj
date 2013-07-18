package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.Stmt
import de.leanovate.jbj.runtime.{SuccessExecResult, Context}
import de.leanovate.jbj.runtime.value.{UndefinedVal, ValueRef}

case class AssignStmt(assignments: List[Assignment]) extends Stmt {
  override def exec(ctx: Context) = {
    assignments.foreach {
      assignment =>
        val value = assignment.expr.map(_.eval(ctx)).getOrElse(UndefinedVal)
        ctx.findVariable(assignment.variableName) match {
          case Some(valueRef) => valueRef.value = value.copy
          case None => ctx.defineVariable(assignment.variableName, static = false, ValueRef(value.copy))
          case _ =>
        }
    }
    SuccessExecResult()
  }
}
