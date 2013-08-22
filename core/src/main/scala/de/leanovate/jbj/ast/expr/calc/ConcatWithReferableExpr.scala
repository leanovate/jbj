package de.leanovate.jbj.ast.expr.calc

import de.leanovate.jbj.ast.{Expr, ReferableExpr}
import de.leanovate.jbj.runtime.Reference
import de.leanovate.jbj.runtime.value.PAny
import java.io.PrintStream
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.ast.expr.BinaryReferableExpr

case class ConcatWithReferableExpr(reference: ReferableExpr, expr: Expr) extends BinaryReferableExpr {
  override def eval(implicit ctx: Context) = {
    val result = reference.evalOld.toStr __ expr.evalOld.toStr
    reference.evalRef.assign(result)
    result
  }

}