package de.leanovate.jbj.ast.name

import de.leanovate.jbj.ast.{Name, Expr}
import java.io.PrintStream
import de.leanovate.jbj.runtime.context.Context

case class DynamicName(expr: Expr) extends Name {
  override def evalName(implicit ctx: Context) = expr.evalOld.toStr.asString

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    expr.dump(out, ident + "  ")
  }
}
