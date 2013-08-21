package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{NodePosition, Expr, ReferableExpr}
import de.leanovate.jbj.runtime.{Reference}
import de.leanovate.jbj.runtime.value.{PVar, PAny}
import java.io.PrintStream
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.context.Context

case class AssignReferableExpr(reference: ReferableExpr, expr: Expr) extends ReferableExpr {
  override def eval(implicit ctx: Context) = {
    reference.evalRef.assign(expr.evalOld).asVal
  }

  override def evalRef(implicit ctx: Context) = new Reference {
    val result = reference.evalRef.assign(expr.evalOld)

    def asVal = result.asVal

    def asVar = result

    def assign(pAny: PAny) = pAny

    def unset() {
      throw new FatalErrorJbjException("Can't use function return value in write context")
    }
  }

  override def toXml =
    <AssignReferableExpr>
      <reference>
        {reference.toXml}
      </reference>
      <expr>
        {expr.toXml}
      </expr>
    </AssignReferableExpr>
}
