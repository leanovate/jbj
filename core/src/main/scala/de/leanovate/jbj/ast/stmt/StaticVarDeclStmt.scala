package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{StaticInitializer, Stmt}
import de.leanovate.jbj.runtime.{SuccessExecResult, Context}
import de.leanovate.jbj.runtime.value.ValueRef

case class StaticVarDeclStmt(assignments: List[StaticAssignment])
  extends Stmt with StaticInitializer {

  override def exec(implicit ctx: Context) = {
    assignments.foreach {
      assignment =>
        val valueRef = ctx.static.findVariable(assignment.variableName).getOrElse(ValueRef())
        ctx.defineVariable(assignment.variableName, valueRef)
    }
    SuccessExecResult
  }

  override def initializeStatic(implicit ctx: Context) {
    assignments.foreach {
      assignment =>
        ctx.static.defineVariable(assignment.variableName, ValueRef(assignment.initial.map(_.eval)))
    }
  }
}