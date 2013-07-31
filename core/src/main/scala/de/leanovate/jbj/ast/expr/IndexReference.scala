package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Expr, Reference}
import de.leanovate.jbj.runtime.{Value, Context}
import de.leanovate.jbj.runtime.value.UndefinedVal

case class IndexReference(reference: Reference, indexExpr: Option[Expr]) extends Reference {
  override def eval(ctx: Context) = {
    val array = reference.eval(ctx)

    array.getAt(indexExpr.map(_.eval(ctx)).getOrElse(UndefinedVal))
  }

  override def assign(ctx: Context, value: Value) {}
}
