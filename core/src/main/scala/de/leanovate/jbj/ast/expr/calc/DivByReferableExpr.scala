package de.leanovate.jbj.ast.expr.calc

import de.leanovate.jbj.ast.{Expr, ReferableExpr}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.PAny
import java.io.PrintStream

case class DivByReferableExpr(reference: ReferableExpr, expr: Expr) extends ReferableExpr {
  override def eval(implicit ctx: Context) = {
    val result = reference.eval.toNum / expr.eval.toNum
    reference.assignVar(result)
    result
  }

  override def evalVar(implicit ctx: Context) = eval

  override def assignVar(pAny: PAny)(implicit ctx: Context) {
  }

  override def unsetVar(implicit ctx: Context) {}

  override def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName + " " + position)
    reference.dump(out, ident + "  ")
    expr.dump(out, ident + "  ")
  }
}
