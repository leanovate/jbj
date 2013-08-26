package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{Expr, ReferableExpr}
import de.leanovate.jbj.core.runtime.Reference
import de.leanovate.jbj.core.runtime.value.PAny
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.core.runtime.context.Context

case class AssignReferableExpr(reference: ReferableExpr, expr: Expr) extends ReferableExpr {
  override def eval(implicit ctx: Context) = {
    reference.evalRef.assign(expr.eval.asVal.copy)
  }

  override def evalRef(implicit ctx: Context) = new Reference {
    val result = reference.evalRef.assign(expr.eval.asVal.copy)

    def isDefined = !asVal.isNull

    def asVal = result.asVal

    def asVar = result

    def assign(pAny: PAny)(implicit ctx:Context) = pAny

    def unset() {
      throw new FatalErrorJbjException("Can't use function return value in write context")
    }
  }
}
