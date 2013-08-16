package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Name, Stmt}
import de.leanovate.jbj.runtime.{SuccessExecResult, Context}
import de.leanovate.jbj.runtime.value.VarRef

case class GlobalVarDeclAssignStmt(variableNames: List[Name]) extends Stmt {
  override def exec(implicit ctx: Context) = {
    variableNames.foreach {
      variableName =>
        val name = variableName.evalName
        val valueRef = ctx.global.findVariable(name) match {
          case Some(ref) => ref
          case None =>
            val initial = VarRef()
            ctx.global.defineVariable(name, initial)
            initial
        }
        ctx.defineVariable(name, valueRef)
    }
    SuccessExecResult
  }
}
