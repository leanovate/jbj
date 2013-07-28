package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{FilePosition, Expr}
import de.leanovate.jbj.runtime.{ValueRef, Context}
import de.leanovate.jbj.runtime.value.IntegerVal

case class VarDecrAndGetExpr(position: FilePosition, variableName: String) extends Expr {
  def eval(ctx: Context) = {
    ctx.findVariable(variableName) match {
      case Some(valueRef) =>
        valueRef.value = valueRef.value.decr
        valueRef.value
      case None =>
        ctx.defineVariable(variableName, ValueRef(IntegerVal(1)))
        IntegerVal(0)
    }
  }
}
