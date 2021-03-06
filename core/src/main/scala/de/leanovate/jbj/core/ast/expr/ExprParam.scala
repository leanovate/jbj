package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.core.ast.{RefExpr, Expr}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.types.PParam

class ExprParam(expr: Expr)(implicit ctx: Context) extends PParam {
  override def hasRef = false

  override def byRef = None

  override def byVal = expr.eval.asVal
}

class ReferableExprParam(referableExpr: RefExpr)(implicit ctx: Context) extends PParam {
  override def hasRef = true

  override def byRef = {
    val ref = referableExpr.evalRef
    if (ref.isConstant)
      Some(ref.asVal)
    else
      Some(ref.asVar)
  }

  override def byVal = referableExpr.eval.asVal
}

object ExprParam {
  def apply(expr: Expr)(implicit ctx: Context): PParam = expr match {
    case referableExpr: RefExpr => new ReferableExprParam(referableExpr)
    case _ => new ExprParam(expr)
  }
}