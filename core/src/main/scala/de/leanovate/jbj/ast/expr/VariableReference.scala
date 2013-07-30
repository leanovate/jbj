package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, Reference}
import de.leanovate.jbj.runtime.{ValueRef, Value, Context}
import de.leanovate.jbj.runtime.value.UndefinedVal

case class VariableReference(variableName: Name) extends Reference {
  override def eval(ctx: Context) = ctx.findVariable(variableName.evalName(ctx)).map(_.value).getOrElse(UndefinedVal)

  override def assign(ctx: Context, value: Value) {
    var name = variableName.evalName(ctx)
    ctx.findVariable(name) match {
      case Some(valueRef) => valueRef.value = value
      case None => ctx.defineVariable(name, ValueRef(value))
      case _ =>
    }
  }
}
