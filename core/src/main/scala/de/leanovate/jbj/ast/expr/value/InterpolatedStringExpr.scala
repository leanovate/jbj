package de.leanovate.jbj.ast.expr.value

import de.leanovate.jbj.ast.Expr
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.StringVal
import de.leanovate.jbj.parser.{ParseContext, JbjParser}

case class InterpolatedStringExpr(format: String, interpolations: List[Expr]) extends Expr {
  override def eval(implicit ctx: Context) = {
    val values = interpolations.map(_.eval.toStr.value)
    StringVal(format.format(values: _*))
  }
}

object InterpolatedStringExpr {
  def apply(parseCtx: ParseContext, charOrInterpolations: List[Either[Char, String]]): Expr = {
    val str = charOrInterpolations.foldLeft(StringBuilder.newBuilder) {
      case (b, Left('%')) =>
        b ++= "%%"
      case (b, Left(ch)) =>
        b += ch
      case (b, Right(_)) =>
        b ++= "%s"
    }.result()
    val interpolations = charOrInterpolations.filter(_.isRight).map {
      s => new JbjParser(parseCtx).parseExpr(s.right.get)
    }

    if (interpolations.isEmpty)
      ScalarExpr(StringVal(str))
    else
      InterpolatedStringExpr(str, interpolations)
  }
}