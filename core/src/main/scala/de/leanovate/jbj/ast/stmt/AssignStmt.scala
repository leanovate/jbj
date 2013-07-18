package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.Stmt
import de.leanovate.jbj.runtime.{SuccessExecResult, Context}
import de.leanovate.jbj.ast.value.ValueRef

case class AssignStmt(assignments: List[Assignment], static: Boolean) extends Stmt {
  override def exec(ctx: Context) = {
    assignments.foreach {
      assignment =>
        val value = assignment.expr.eval(ctx)
        ctx.findVariable(assignment.variableName) match {
          case Some(valueRef) if !static => valueRef.value = value.copy
          case None => ctx.defineVariable(assignment.variableName, static, ValueRef(value.copy))
          case _ =>
        }
    }
    SuccessExecResult()
  }
}
