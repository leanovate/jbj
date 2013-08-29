/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr.value

import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.runtime.value.StringVal
import de.leanovate.jbj.core.runtime.context.Context

case class InterpolatedStringExpr(interpolatedStr: List[Either[String, Expr]]) extends Expr {
  lazy val format = interpolatedStr.map {
    case Left(str) => str.replace("%", "%%")
    case Right(_) => "%s"
  }.mkString("")

  lazy val interpolations = interpolatedStr.filter(_.isRight).map(_.right.get)

  override def eval(implicit ctx: Context) = {
    val values = interpolations.map(_.eval.asVal.toStr.asString)
    StringVal(format.format(values: _*))
  }
}
