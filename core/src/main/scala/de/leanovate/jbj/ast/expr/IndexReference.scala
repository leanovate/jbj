package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Expr, Reference}
import de.leanovate.jbj.runtime.{ArrayKey, Value, Context}
import de.leanovate.jbj.runtime.value.UndefinedVal

case class IndexReference(reference: Reference, indexExpr: Option[Expr]) extends Reference {
  override def eval(ctx: Context) = {
    val optArrayKey = indexExpr.flatMap {
      expr =>
        ArrayKey(expr.eval(ctx))
    }

    optArrayKey.map {
      arrayKey =>
        val array = reference.eval(ctx)
        array.getAt(arrayKey)
    }.getOrElse(UndefinedVal)
  }

  override def assign(ctx: Context, value: Value) {
    val optArrayKey = indexExpr.flatMap {
      expr =>
        ArrayKey(expr.eval(ctx))
    }

    optArrayKey.foreach {
      arrayKey =>
        val array = reference.eval(ctx)
        array.setAt(arrayKey, value)
    }
  }
}
