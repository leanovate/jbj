package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Name, MemberModifier, FilePosition, Stmt}
import de.leanovate.jbj.runtime.{ValueRef, SuccessExecResult, Context}
import de.leanovate.jbj.runtime.value.{UndefinedVal}

case class GlobalVarDeclAssignStmt(position: FilePosition, variableNames: List[String]) extends Stmt {
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
