package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Expr, Reference}
import de.leanovate.jbj.runtime.{ArrayKey, Value, Context}
import de.leanovate.jbj.runtime.value.UndefinedVal

case class IndexReference(reference: Reference, indexExpr: Option[Expr]) extends Reference {
  override def eval(implicit ctx: Context) = {
    val optArrayKey = indexExpr.flatMap {
      expr =>
        ArrayKey(expr.eval)
    }

    optArrayKey.map {
      arrayKey =>
        val array = reference.eval
        array.getAt(arrayKey)
    }.getOrElse(UndefinedVal)
  }

  override def assign( value: Value)(implicit ctx:Context) {
    val optArrayKey = indexExpr.flatMap {
      expr =>
        ArrayKey(expr.eval)
    }

    optArrayKey.foreach {
      arrayKey =>
        val array = reference.eval
        array.setAt(Some(arrayKey), value)
    }
  }
}
