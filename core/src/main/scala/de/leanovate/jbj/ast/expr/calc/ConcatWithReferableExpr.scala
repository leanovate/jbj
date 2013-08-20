package de.leanovate.jbj.ast.expr.calc

import de.leanovate.jbj.ast.{Expr, ReferableExpr}
import de.leanovate.jbj.runtime.{Reference, Context}
import de.leanovate.jbj.runtime.value.PAny
import java.io.PrintStream

case class ConcatWithReferableExpr(reference: ReferableExpr, expr: Expr) extends ReferableExpr {
  override def eval(implicit ctx: Context) = {
    val result = reference.eval.toStr dot expr.eval.toStr
    reference.assignVar(result)
    result
  }

  override def evalRef(implicit ctx: Context): Reference = new Reference {
    val result = eval

    def asVal = result

    def asVar = result

    def assign(pAny: PAny) = {
      pAny
    }

    def unset() {
    }
  }

  override def evalVar(implicit ctx: Context) = eval

  override def assignVar(pAny: PAny)(implicit ctx: Context) {
  }

  override def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName + " " + position)
    reference.dump(out, ident + "  ")
    expr.dump(out, ident + "  ")
  }

}