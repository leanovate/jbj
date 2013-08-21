package de.leanovate.jbj.ast.expr.calc

import de.leanovate.jbj.ast.{Expr, ReferableExpr}
import de.leanovate.jbj.runtime.{Reference}
import de.leanovate.jbj.runtime.value.PAny
import java.io.PrintStream
import de.leanovate.jbj.runtime.context.Context

case class AddToReferableExpr(reference: ReferableExpr, expr: Expr) extends ReferableExpr {
  override def eval(implicit ctx: Context) = {
    val result = reference.evalOld.toNum + expr.evalOld.toNum
    reference.evalRef.assign(result)
    result
  }

  override def evalRef(implicit ctx: Context): Reference = new Reference {
    val result = evalOld

    def asVal = result

    def asVar = result

    def assign(pAny: PAny) = {
      pAny
    }

    def unset() {
    }
  }

  override def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName)
    reference.dump(out, ident + "  ")
    expr.dump(out, ident + "  ")
  }
}
