package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Reference, NodePosition}
import de.leanovate.jbj.runtime.{ValueRef, Value, Context}
import de.leanovate.jbj.runtime.value.UndefinedVal

case class VariableReference(variableName: String) extends Reference {
  def eval(ctx: Context) = ctx.findVariable(variableName).map(_.value).getOrElse(UndefinedVal)

  def assignInitial(ctx: Context, value: Value) {
    ctx.findVariable(variableName) match {
      case None => ctx.defineVariable(variableName, ValueRef(value))
      case _ =>
    }
  }

  def assign(ctx: Context, value: Value) {
    ctx.findVariable(variableName) match {
      case Some(valueRef) => valueRef.value = value
      case None => ctx.defineVariable(variableName, ValueRef(value))
      case _ =>
    }
  }
}
