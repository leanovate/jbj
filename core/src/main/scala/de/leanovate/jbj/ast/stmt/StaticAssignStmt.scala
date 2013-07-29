package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{FilePosition, Stmt}
import de.leanovate.jbj.runtime.{ValueRef, SuccessExecResult, Context}
import de.leanovate.jbj.runtime.value.UndefinedVal

case class StaticAssignStmt(position: FilePosition, assignments: List[Assignment]) extends Stmt {
  override def exec(ctx: Context) = {
    assignments.foreach {
      assignment =>
        val value = assignment.expr.map(_.eval(ctx)).getOrElse(UndefinedVal)
        val name = assignment.variableName.evalName(ctx)
        val valueRef = ctx.static.findVariable(name) match {
          case Some(ref) => ref
          case None =>
            val initial = ValueRef(value)
            ctx.static.defineVariable(name, initial)
            initial
        }
        ctx.defineVariable(name, valueRef)
    }
    SuccessExecResult()
  }
}