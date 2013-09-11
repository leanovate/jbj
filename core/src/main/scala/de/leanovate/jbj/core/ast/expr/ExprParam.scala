package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.runtime.PParam
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.core.ast.{ReferableExpr, Expr}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

class ExprParam(expr: Expr)(implicit ctx: Context) extends PParam {
  override def hasRef = false

  override def byRef = {
    throw new FatalErrorJbjException("Only variables can be passed by reference")
  }

  override def byVal = expr.eval.asVal
}

class ReferableExprParam(referableExpr: ReferableExpr)(implicit ctx: Context) extends PParam {
  override def hasRef = true

  override def byRef = {
    val ref = referableExpr.evalRef
    if (ref.isConstant)
      ref.byVal
    else
      ref.byVar
  }

  override def byVal = referableExpr.eval.asVal
}

object ExprParam {
  def apply(expr: Expr)(implicit ctx: Context): PParam = expr match {
    case referableExpr: ReferableExpr => new ReferableExprParam(referableExpr)
    case _ => new ExprParam(expr)
  }
}