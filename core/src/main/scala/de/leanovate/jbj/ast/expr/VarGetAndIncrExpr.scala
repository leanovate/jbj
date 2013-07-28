package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{FilePosition, Expr}
import de.leanovate.jbj.runtime.{ValueRef, Context}
import de.leanovate.jbj.runtime.value.{IntegerVal}

case class VarGetAndIncrExpr(position: FilePosition, variableName: String) extends Expr {
  def eval(ctx: Context) = {
    ctx.findVariable(variableName) match {
      case Some(valueRef) =>
        val result = valueRef.value
        valueRef.value = result.incr
        result
      case None =>
        ctx.defineVariable(variableName, ValueRef(IntegerVal(1)))
        IntegerVal(0)
    }
  }
}
