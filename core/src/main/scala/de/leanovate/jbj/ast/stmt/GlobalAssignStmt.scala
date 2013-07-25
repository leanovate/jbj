package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{FilePosition, Stmt}
import de.leanovate.jbj.runtime.{SuccessExecResult, Context}
import de.leanovate.jbj.runtime.value.{UndefinedVal, ValueRef}

case class GlobalAssignStmt(position:FilePosition,assignments: List[Assignment]) extends Stmt {
  override def exec(ctx: Context) = {
    assignments.foreach {
      assignment =>
        val value = assignment.expr.map(_.eval(ctx)).getOrElse(UndefinedVal)
        ctx.global.findVariable(assignment.variableName) match {
          case None => ctx.global.defineVariable(assignment.variableName, ValueRef(value.copy))
          case _ =>
        }
    }
    SuccessExecResult()
  }
}
