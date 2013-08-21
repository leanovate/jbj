package de.leanovate.jbj.ast.name

import de.leanovate.jbj.ast.{Name, Expr}
import de.leanovate.jbj.runtime.Context
import java.io.PrintStream

case class DynamicName(expr: Expr) extends Name {
  override def evalName(implicit ctx: Context) = expr.evalOld.toStr.asString

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    expr.dump(out, ident + "  ")
  }
}
