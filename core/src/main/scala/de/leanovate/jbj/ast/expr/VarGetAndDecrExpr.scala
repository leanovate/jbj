package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.value.{IntegerVal, ValueRef}

case class VarGetAndDecrExpr (variableName: String) extends Expr {
  def eval(ctx: Context) = {
    ctx.findVariable(variableName) match {
      case Some(valueRef) =>
        val result = valueRef.copy
        valueRef.value = result.decr
        result
      case None =>
        ctx.defineVariable(variableName, static = false, ValueRef(IntegerVal(1)))
        IntegerVal(0)
    }
  }
}
