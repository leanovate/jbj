package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Name, Stmt}
import de.leanovate.jbj.runtime.{SuccessExecResult}
import de.leanovate.jbj.runtime.value.PVar
import de.leanovate.jbj.runtime.context.Context

case class GlobalVarDeclAssignStmt(variableNames: List[Name]) extends Stmt {
  override def exec(implicit ctx: Context) = {
    variableNames.foreach {
      variableName =>
        val name = variableName.evalName
        val valueRef = ctx.global.findVariable(name) match {
          case Some(ref) => ref
          case None =>
            val initial = PVar()
            ctx.global.defineVariable(name, initial)
            initial
        }
        ctx.defineVariable(name, valueRef)
    }
    SuccessExecResult
  }
}
