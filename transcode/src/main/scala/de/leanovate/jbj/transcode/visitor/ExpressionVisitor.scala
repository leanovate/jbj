package de.leanovate.jbj.transcode.visitor

import de.leanovate.jbj.core.ast.{Expr, NodeVisitor}
import scala.text.Document
import scala.text.Document._
import de.leanovate.jbj.core.ast.expr.PrintExpr
import de.leanovate.jbj.core.ast.expr.value.ScalarExpr
import de.leanovate.jbj.transcode.builders.LiteralBuilder
import de.leanovate.jbj.core.ast.expr.calc.ConcatExpr

class ExpressionVisitor extends NodeVisitor[Document] {
  val expressions = Seq.newBuilder[Document]

  override def result = expressions.result().reduceOption(_ :: _).getOrElse(empty)

  override def visit = {
    case PrintExpr(expr) =>
      expressions += text("ctx.out.print(") :: expr.foldWith(new ExpressionVisitor) :: text(".toOutput)")
      acceptsNextSibling

    case ScalarExpr(value) =>
      expressions += LiteralBuilder.build(value)
      acceptsNextSibling

    case ConcatExpr(left, right) =>
      expressions += text("(") :: left.foldWith(new ExpressionVisitor) :: " __ " :: right.foldWith(new ExpressionVisitor) :: text(")")
      acceptsNextSibling

    case stmt =>
      println("Unhandled node: " + stmt)
      abort
  }
}
