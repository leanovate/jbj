package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.runtime.{Context, Value}
import de.leanovate.jbj.ast.Expr
import java.io.PrintStream

case class ScalarExpr(value: Value) extends Expr {
  override def eval(implicit ctx: Context) = value

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    value.toDump(out, ident + "  ")
  }
}
