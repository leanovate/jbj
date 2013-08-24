package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{StaticInitializer, Stmt}
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.runtime.value.PVar
import de.leanovate.jbj.runtime.context.{Context, StaticContext}

case class StaticVarDeclStmt(assignments: List[StaticAssignment])
  extends Stmt with StaticInitializer {

  override def exec(implicit ctx: Context) = {
    assignments.foreach {
      assignment =>
        val name = assignment.variableName
        val valueRef = ctx.static.findVariable(name).getOrElse {
          val pVar = PVar()
          ctx.static.defineVariable(name, pVar)
          pVar
        }
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