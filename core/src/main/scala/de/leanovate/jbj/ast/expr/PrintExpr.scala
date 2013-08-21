package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.value.IntegerVal
import java.io.PrintStream
import de.leanovate.jbj.runtime.context.Context

case class PrintExpr(expr: Expr) extends Expr {
  override def eval(implicit ctx: Context) = {
    ctx.out.print(expr.evalOld.toOutput)
    IntegerVal(1)
  }

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    expr.dump(out, ident + "  ")
  }
}
