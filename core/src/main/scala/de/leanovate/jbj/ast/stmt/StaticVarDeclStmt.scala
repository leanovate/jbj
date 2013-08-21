package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{StaticInitializer, Stmt}
import de.leanovate.jbj.runtime.{SuccessExecResult, Context}
import de.leanovate.jbj.runtime.value.PVar
import de.leanovate.jbj.runtime.context.StaticContext

case class StaticVarDeclStmt(assignments: List[StaticAssignment])
  extends Stmt with StaticInitializer {

  override def exec(implicit ctx: Context) = {
    assignments.foreach {
      assignment =>
        val valueRef = ctx.static.findVariable(assignment.variableName).getOrElse(PVar())
        ctx.defineVariable(assignment.variableName, valueRef)
    }
    SuccessExecResult
  }

  override def initializeStatic(staticCtx: StaticContext)(implicit ctx:Context) {
    assignments.foreach {
      assignment =>
        staticCtx.defineVariable(assignment.variableName, PVar(assignment.initial.map(_.evalOld)))
    }
  }
}