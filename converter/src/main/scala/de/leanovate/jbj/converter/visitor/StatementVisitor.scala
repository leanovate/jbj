package de.leanovate.jbj.converter.visitor

import de.leanovate.jbj.core.ast.{Expr, NodeVisitor}
import scala.text.Document
import scala.text.Document._
import de.leanovate.jbj.core.ast.stmt.{EchoStmt, InlineStmt, ExprStmt}
import de.leanovate.jbj.converter.builders.{CodeUnitBuilder, StatementBuilder}

class StatementVisitor(implicit builder: CodeUnitBuilder) extends NodeVisitor[Document] {
  val statements = Seq.newBuilder[Document]

  override def result = statements.result().reduceOption(_ :: _).getOrElse(empty)

  override def visit = {
    case ExprStmt(expr: Expr) =>
      statements += expr.foldWith(new ExpressionVisitor)
      acceptsNextSibling

    case InlineStmt(text) =>
      statements += StatementBuilder.inlineStmt(text)
      acceptsNextSibling

    case EchoStmt(exprs) =>
      statements += "echo(" :: exprs.map(_.foldWith(new ExpressionVisitor)).reduceOption(_ :: ", " :: _).getOrElse(empty) :: ")" :: empty
      acceptsNextSibling

    case stmt =>
      println("Unhandled node: " + stmt)
      abort
  }
}
