package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.{StaticInitializer, Stmt}
import de.leanovate.jbj.core.runtime.SuccessExecResult
import de.leanovate.jbj.core.runtime.value.PVar
import de.leanovate.jbj.core.runtime.context.{Context, StaticContext}

case class StaticVarDeclStmt(assignments: List[StaticAssignment])
  extends Stmt with StaticInitializer {

  override def exec(implicit ctx: Context) = {
    assignments.foreach {
      assignment =>
        val name = assignment.variableName
        val valueRef = ctx.static.findOrDefineVariable(name)
        ctx.defineVariable(name, valueRef)
    }
    SuccessExecResult
  }

  override def initializeStatic(staticCtx: StaticContext)(implicit ctx: Context) {
    assignments.foreach {
      assignment =>
        staticCtx.defineVariable(assignment.variableName, PVar(assignment.initial.map(_.eval.asVal)))
    }
  }
}