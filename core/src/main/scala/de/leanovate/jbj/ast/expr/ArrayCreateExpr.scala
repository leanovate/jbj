package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{ReferableExpr, Expr}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.ArrayVal

case class ArrayKeyValue(key: Option[Expr], value: Expr, isRef: Boolean)

case class ArrayCreateExpr(keyValueExprs: List[ArrayKeyValue]) extends Expr {
  override def eval(implicit ctx: Context) = {
    ArrayVal(keyValueExprs.map {
      case ArrayKeyValue(keyExpr, valueExpr: ReferableExpr, true) => (keyExpr.map(_.evalOld), valueExpr.evalRef.asVar)
      case ArrayKeyValue(keyExpr, valueExpr: Expr, _) => (keyExpr.map(_.evalOld), valueExpr.evalOld)
    }: _*)
  }
}
