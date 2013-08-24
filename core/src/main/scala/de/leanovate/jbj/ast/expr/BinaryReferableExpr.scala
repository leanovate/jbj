package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{ReferableExpr, Expr}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.Reference
import de.leanovate.jbj.runtime.value.PAny
import java.io.PrintStream

trait BinaryReferableExpr extends ReferableExpr {
  def reference: ReferableExpr

  def expr: Expr

  override def evalRef(implicit ctx: Context): Reference = new Reference {
    val result = eval

    def isDefined = !asVal.isNull

    def asVal = result.asVal

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
