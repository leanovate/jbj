/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{RefExpr, Expr}
import de.leanovate.jbj.runtime.value.ArrayVal
import de.leanovate.jbj.runtime.context.Context

case class ArrayKeyValue(key: Option[Expr], value: Expr, isRef: Boolean) {
  def phpStr = key.map(_.phpStr + " => ") + (if (isRef) "&" else "") + value.phpStr
}

case class ArrayCreateExpr(keyValueExprs: List[ArrayKeyValue]) extends Expr {
  override def eval(implicit ctx: Context) = {
    ArrayVal(keyValueExprs.map {
      case ArrayKeyValue(keyExpr, valueExpr: RefExpr, true) => (keyExpr.map(_.eval.asVal), valueExpr.evalRef.asVar)
      case ArrayKeyValue(keyExpr, valueExpr: Expr, _) => (keyExpr.map(_.eval.asVal), valueExpr.eval.asVal)
    }: _*)
  }

  override def phpStr = keyValueExprs.map(_.phpStr).mkString("array(", ", ", ")")
}
