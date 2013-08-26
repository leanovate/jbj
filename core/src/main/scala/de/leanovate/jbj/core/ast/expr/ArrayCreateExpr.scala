package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{ReferableExpr, Expr}
import de.leanovate.jbj.core.runtime.value.ArrayVal
import de.leanovate.jbj.core.runtime.context.Context

case class ArrayKeyValue(key: Option[Expr], value: Expr, isRef: Boolean) {
}

case class ArrayCreateExpr(keyValueExprs: List[ArrayKeyValue]) extends Expr {
  override def eval(implicit ctx: Context) = {
    ArrayVal(keyValueExprs.map {
      case ArrayKeyValue(keyExpr, valueExpr: ReferableExpr, true) => (keyExpr.map(_.eval.asVal), valueExpr.evalRef.asVar)
      case ArrayKeyValue(keyExpr, valueExpr: Expr, _) => (keyExpr.map(_.eval.asVal), valueExpr.eval.asVal)
    }: _*)
  }
}
