package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Expr, Reference}
import de.leanovate.jbj.runtime.{Value, Context}

case class IndexReference(reference: Reference, indexExpr: Expr) extends Reference {
  def position = reference.position

  def eval(ctx: Context) = {
    val array = reference.eval(ctx)

    array.getAt(indexExpr.eval(ctx))
  }

  def assignInitial(ctx: Context, value: Value) {

  }

  def assign(ctx: Context, value: Value) {}
}
