package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{FilePosition, Expr}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.{ValueRef, IntegerVal}

case class VarGetAndIncrExpr(position:FilePosition, variableName: String) extends Expr {
  def eval(ctx: Context) = {
    ctx.findVariable(variableName) match {
      case Some(valueRef) =>
        val result = valueRef.copy
        valueRef.value = result.incr
        result
      case None =>
        ctx.defineVariable(variableName, ValueRef(IntegerVal(1)))
        IntegerVal(0)
    }
  }
}
