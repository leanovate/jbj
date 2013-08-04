package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, Reference}
import de.leanovate.jbj.runtime.{ValueRef, Value, Context}
import java.io.PrintStream
import de.leanovate.jbj.runtime.value.NullVal

case class VariableReference(variableName: Name) extends Reference {
  override def eval(implicit ctx: Context) = ctx.findVariable(variableName.evalName).map(_.value).getOrElse(NullVal)

  override def assign(value: Value)(implicit ctx: Context) {
    var name = variableName.evalName
    ctx.findVariable(name) match {
      case Some(valueRef) => valueRef.value = value
      case None => ctx.defineVariable(name, ValueRef(value))
      case _ =>
    }
  }

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    variableName.dump(out, ident + "  ")
  }
}
