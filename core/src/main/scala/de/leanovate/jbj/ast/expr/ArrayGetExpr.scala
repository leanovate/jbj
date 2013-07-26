package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.{FilePosition, Expr}
import de.leanovate.jbj.runtime.value.UndefinedVal

case class ArrayGetExpr(position: FilePosition, variableName: String, indices: List[Expr]) extends Expr {
  def eval(ctx: Context) = {
    val array = ctx.findVariable(variableName).getOrElse(UndefinedVal)

    indices.foldLeft(array) {
      (array, indexExpr) =>
        array.getAt(indexExpr.eval(ctx))
    }
  }
}
