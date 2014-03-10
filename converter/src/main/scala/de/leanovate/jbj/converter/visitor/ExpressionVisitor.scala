package de.leanovate.jbj.converter.visitor

import de.leanovate.jbj.core.ast.{Expr, NodeVisitor}
import scala.text.Document
import scala.text.Document._
import de.leanovate.jbj.core.ast.expr.{Precedence, BinaryExpr, PrintExpr}
import de.leanovate.jbj.core.ast.expr.value.ScalarExpr
import de.leanovate.jbj.converter.builders.LiteralBuilder
import de.leanovate.jbj.core.ast.expr.calc.ConcatExpr

class ExpressionVisitor extends NodeVisitor[Document] {
  val expressions = Seq.newBuilder[Document]

  override def result = expressions.result().reduceOption(_ :: _).getOrElse(empty)

  override def visit = {
    case PrintExpr(expr) =>
      expressions += text("print(") :: expr.foldWith(new ExpressionVisitor) :: text(")")
      acceptsNextSibling

    case ScalarExpr(value) =>
      expressions += LiteralBuilder.build(value)
      acceptsNextSibling

    case ConcatExpr(left, right) =>
      expressions += parentesis(Precedence.AddSub)(left) :: " !! " :: parentesis(Precedence.AddSub)(right)
      acceptsNextSibling

    case stmt =>
      println("Unhandled node: " + stmt)
      abort
  }

  def parentesis(threshold: Precedence.Type): PartialFunction[Expr, Document] = {
    case expr: BinaryExpr if expr.precedence.id < threshold.id =>
      text("(") :: expr.foldWith(new ExpressionVisitor) :: text(")")
    case expr: Expr => expr.foldWith(new ExpressionVisitor)
  }
}
