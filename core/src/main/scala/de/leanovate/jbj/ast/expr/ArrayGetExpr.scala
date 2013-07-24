package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.runtime.{Context, Value}
import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.value.UndefinedVal

case class ArrayGetExpr(variableName: String, indices: List[Expr]) extends Expr {
  def eval(ctx: Context) = {
    val array = ctx.findVariable(variableName).getOrElse(UndefinedVal)

    indices.foldLeft(array) {
      (array, indexExpr) =>
        array.getAt(indexExpr.eval(ctx))
    }
  }
}
