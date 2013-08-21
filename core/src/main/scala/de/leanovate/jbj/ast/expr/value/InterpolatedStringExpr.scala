package de.leanovate.jbj.ast.expr.value

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.StringVal

case class InterpolatedStringExpr(interpolatedStr: List[Either[String, Expr]]) extends Expr {
  lazy val format = interpolatedStr.map {
    case Left(str) => str.replace("%", "%%")
    case Right(_) => "%s"
  }.mkString("")

  lazy val interpolations = interpolatedStr.filter(_.isRight).map(_.right.get)

  override def eval(implicit ctx: Context) = {
    val values = interpolations.map(_.evalOld.toStr.asString)
    StringVal(format.format(values: _*))
  }
}
