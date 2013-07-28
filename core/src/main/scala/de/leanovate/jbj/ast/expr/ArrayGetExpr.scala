package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.{Reference, FilePosition, Expr}

case class ArrayGetExpr(position: FilePosition, reference: Reference, indices: List[Expr]) extends Expr {
  def eval(ctx: Context) = {
    val array = reference.eval(ctx)

    indices.foldLeft(array) {
      (array, indexExpr) =>
        array.getAt(indexExpr.eval(ctx))
    }
  }
}
