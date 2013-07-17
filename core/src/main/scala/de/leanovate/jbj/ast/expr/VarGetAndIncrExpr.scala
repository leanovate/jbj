package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.exec.Context
import de.leanovate.jbj.ast.value.IntegerVal

case class VarGetAndIncrExpr(variableName: String) extends Expr {
  def eval(ctx: Context) = {
    ctx.findVariable(variableName) match {
      case Some(valueRef) =>
        val result = valueRef.copy
        valueRef.value = result.incr
        result
      case None =>
        ctx.defineVariable(variableName, static = false, new IntegerVal(1))
        IntegerVal(0)
    }
  }
}
