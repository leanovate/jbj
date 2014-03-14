package de.leanovate.jbj.converter.visitor

import de.leanovate.jbj.core.ast.{Expr, NodeVisitor}
import scala.text.Document
import scala.text.Document._
import de.leanovate.jbj.core.ast.expr._
import de.leanovate.jbj.core.ast.expr.value.ScalarExpr
import de.leanovate.jbj.converter.builders.{CodeUnitBuilder, ProgCodeUnitBuilder, LiteralBuilder}
import de.leanovate.jbj.core.ast.expr.calc.{SubExpr, AddExpr, ConcatExpr}
import de.leanovate.jbj.core.ast.expr.value.ScalarExpr
import de.leanovate.jbj.core.ast.expr.AssignRefExpr
import de.leanovate.jbj.core.ast.expr.PrintExpr
import de.leanovate.jbj.core.ast.name.StaticName
import de.leanovate.jbj.core.ast.expr.comp.LtExpr

class ExpressionVisitor(implicit builder: CodeUnitBuilder) extends NodeVisitor[Document] {
  val expressions = Seq.newBuilder[Document]

  override def result = expressions.result().reduceOption(_ :: _).getOrElse(empty)

  override def visit = {

    case AddExpr(left, right) =>
      expressions += parentesis(Precedence.AddSub)(left) :: " + " :: parentesis(Precedence.AddSub)(right)
      acceptsNextSibling

    case ArrayCreateExpr(keyValues) if keyValues.forall(_.key.isEmpty) =>
      expressions += "array(" :: keyValues.map(_.value.foldWith(new ExpressionVisitor)).reduceOption(_ :: ", " :: _).getOrElse(empty) :: ")" :: empty
      acceptsNextSibling

    case ArrayCreateExpr(keyValues) =>
      expressions += "map(" :: keyValues.map {
        case ArrayKeyValue(None, value, _) =>
          "None ->" :: value.foldWith(new ExpressionVisitor)
        case ArrayKeyValue(Some(key), value, _) =>
          "Some(" :: key.foldWith(new ExpressionVisitor) :: ") ->" :: value.foldWith(new ExpressionVisitor)
      }.reduceOption(_ :: ", " :: _).getOrElse(empty) :: ")" :: empty
      acceptsNextSibling

    case AssignRefExpr(refExpr, expr) =>
      expressions += refExpr.foldWith(new ExpressionVisitor) :: ".value = " :: expr.foldWith(new ExpressionVisitor)
      acceptsNextSibling

    case CallByNameRefExpr(name, parameters) =>
      expressions += s"""f("$name")(""" :: parameters.map(_.foldWith(new ExpressionVisitor)).reduceOption(_ :: ", " :: _).getOrElse(empty) :: ")" :: empty
      acceptsNextSibling

    case ConcatExpr(left, right) =>
      expressions += parentesis(Precedence.AddSub)(left) :: " !! " :: parentesis(Precedence.AddSub)(right)
      acceptsNextSibling

    case DimRefExpr(ref, index) =>
      expressions += parentesis(Precedence.Term)(ref) :: ".dim(" :: index.map(_.foldWith(new ExpressionVisitor)).getOrElse(empty) :: ")" :: empty
      acceptsNextSibling

    case GetAndIncrExpr(expr) =>
      expressions += parentesis(Precedence.Term)(expr) :: ".++" :: empty
      acceptsNextSibling

    case LtExpr(left, right) =>
      expressions += parentesis(Precedence.Compare)(left) :: " < " :: parentesis(Precedence.AddSub)(right)
      acceptsNextSibling

    case PrintExpr(expr) =>
      expressions += text("print(") :: expr.foldWith(new ExpressionVisitor) :: text(")")
      acceptsNextSibling

    case ScalarExpr(value) =>
      expressions += LiteralBuilder.build(value)
      acceptsNextSibling

    case SubExpr(left, right) =>
      expressions += parentesis(Precedence.AddSub)(left) :: " - " :: parentesis(Precedence.AddSub)(right)
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
