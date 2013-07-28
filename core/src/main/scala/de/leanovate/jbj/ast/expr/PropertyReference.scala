package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.Reference
import de.leanovate.jbj.runtime.{Value, Context}

case class PropertyReference(reference: Reference, propertyName: String) extends Reference {
  def position = reference.position

  def eval(ctx: Context) = ???

  def assignInitial(ctx: Context, value: Value) {}

  def assign(ctx: Context, value: Value) {}
}
