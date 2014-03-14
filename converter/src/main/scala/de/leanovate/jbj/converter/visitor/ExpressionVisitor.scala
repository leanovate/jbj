package de.leanovate.jbj.converter.visitor

import de.leanovate.jbj.core.ast.{Expr, NodeVisitor}
import scala.text.Document
import scala.text.Document._
import de.leanovate.jbj.core.ast.expr._
import de.leanovate.jbj.core.ast.expr.value.ScalarExpr
import de.leanovate.jbj.converter.builders.{CodeUnitBuilder, LiteralBuilder}
import de.leanovate.jbj.core.ast.expr.calc.ConcatExpr
import de.leanovate.jbj.core.ast.expr.value.ScalarExpr
import de.leanovate.jbj.core.ast.expr.AssignRefExpr
import de.leanovate.jbj.core.ast.expr.calc.ConcatExpr
import de.leanovate.jbj.core.ast.expr.PrintExpr
import de.leanovate.jbj.core.ast.name.StaticName

class ExpressionVisitor(implicit builder: CodeUnitBuilder) extends NodeVisitor[Document] {
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

    case AssignRefExpr(refExpr, expr) =>
      expressions += refExpr.foldWith(new ExpressionVisitor) :: ".value = " :: expr.foldWith(new ExpressionVisitor)
      acceptsNextSibling

    case VariableRefExpr(StaticName(name)) =>
      builder.defineLocalVar(name)
      expressions += text(name)
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
