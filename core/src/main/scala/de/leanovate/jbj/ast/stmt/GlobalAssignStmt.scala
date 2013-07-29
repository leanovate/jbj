package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Name, Modifier, FilePosition, Stmt}
import de.leanovate.jbj.runtime.{ValueRef, SuccessExecResult, Context}
import de.leanovate.jbj.runtime.value.{UndefinedVal}

case class GlobalAssignStmt(position: FilePosition, variableNames: List[Name]) extends Stmt {
  override def exec(ctx: Context) = {
    variableNames.foreach {
      variableName =>
        val name = variableName.evalName(ctx)
        val valueRef = ctx.global.findVariable(name) match {
          case Some(ref) => ref
          case None =>
            val initial = ValueRef()
            ctx.global.defineVariable(name, initial)
            initial
        }
        ctx.defineVariable(name, valueRef)
    }
    SuccessExecResult()
  }
}
