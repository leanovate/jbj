package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, Reference}
import de.leanovate.jbj.runtime.{Value, Context}

case class PropertyReference(reference: Reference, propertyName: Name) extends Reference {
  override def eval(ctx: Context) = ???

  def assignInitial(ctx: Context, value: Value) {}

  def assign(ctx: Context, value: Value) {}
}
