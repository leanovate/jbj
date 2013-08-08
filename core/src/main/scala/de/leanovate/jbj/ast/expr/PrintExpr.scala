package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.IntegerVal
import java.io.PrintStream

case class PrintExpr(expr: Expr) extends Expr {
  override def eval(implicit ctx: Context) = {
    ctx.out.print(expr.eval.toOutput)
    IntegerVal(1)
  }

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    expr.dump(out, ident + "  ")
  }
}
