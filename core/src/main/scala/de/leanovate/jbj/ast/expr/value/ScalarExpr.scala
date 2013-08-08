package de.leanovate.jbj.ast.expr.value

import de.leanovate.jbj.runtime.{Context}
import de.leanovate.jbj.ast.Expr
import java.io.PrintStream
import de.leanovate.jbj.runtime.value.Value
import de.leanovate.jbj.runtime.buildin.OutputFunctions

case class ScalarExpr(value: Value) extends Expr {
  override def eval(implicit ctx: Context) = value

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    out.println(ident + value.toString)
  }
}
