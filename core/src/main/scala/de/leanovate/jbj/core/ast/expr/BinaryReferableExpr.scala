package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{ReferableExpr, Expr}
import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.core.runtime.Reference
import de.leanovate.jbj.core.runtime.value.PAny
import java.io.PrintStream

trait BinaryReferableExpr extends ReferableExpr {
  def reference: ReferableExpr

  def expr: Expr

  override def evalRef(implicit ctx: Context): Reference = new Reference {
    val result = eval

    def isDefined = !asVal.isNull

    def asVal = result.asVal

    def asVar = result

    def assign(pAny: PAny)(implicit ctx:Context) = {
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
