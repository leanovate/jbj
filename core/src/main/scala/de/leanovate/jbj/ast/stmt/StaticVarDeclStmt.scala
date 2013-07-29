package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{StaticInitializer, FilePosition, Stmt}
import de.leanovate.jbj.runtime.{ValueRef, SuccessExecResult, Context}
import de.leanovate.jbj.runtime.value.UndefinedVal

case class StaticVarDeclStmt(position: FilePosition, assignments: List[StaticAssignment])
  extends Stmt with StaticInitializer {

  override def exec(ctx: Context) = {
    assignments.foreach {
      assignment =>
        val valueRef = ctx.static.findVariable(assignment.variableName).getOrElse(ValueRef())
        ctx.defineVariable(assignment.variableName, valueRef)
    }
    SuccessExecResult()
  }

  def initializeStatic(ctx: Context) {
    assignments.foreach {
      assignment =>
        ctx.static.defineVariable(assignment.variableName, ValueRef(assignment.initial))
    }
  }
}