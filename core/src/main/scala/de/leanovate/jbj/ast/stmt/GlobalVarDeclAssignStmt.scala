package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.Stmt
import de.leanovate.jbj.runtime.{ValueRef, SuccessExecResult, Context}

case class GlobalVarDeclAssignStmt(variableNames: List[String]) extends Stmt {
  override def exec(ctx: Context) = {
    variableNames.foreach {
      variableName =>
        val valueRef = ctx.global.findVariable(variableName) match {
          case Some(ref) => ref
          case None =>
            val initial = ValueRef()
            ctx.global.defineVariable(variableName, initial)
            initial
        }
        ctx.defineVariable(variableName, valueRef)
    }
    SuccessExecResult()
  }
}
