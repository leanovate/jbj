package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Expr, ReferableExpr}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.{PVar, PAny}
import java.io.PrintStream
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class AssignReferableExpr(reference: ReferableExpr, expr: Expr) extends ReferableExpr {
  override def eval(implicit ctx: Context) = {
    reference.evalRef.assign(expr.eval).asVal
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
