package de.leanovate.jbj.transcode.visitor

import de.leanovate.jbj.core.ast.{Expr, NodeVisitor}
import scala.text.Document
import scala.text.Document._
import de.leanovate.jbj.core.ast.stmt.{InlineStmt, ExprStmt}
import de.leanovate.jbj.transcode.builders.StatementBuilder

class StatementVisitor extends NodeVisitor[Document] {
  val statements = Seq.newBuilder[Document]

  override def result = statements.result().reduceOption(_ :: _).getOrElse(empty)

  override def visit = {
    case ExprStmt(expr: Expr) =>
      statements += expr.foldWith(new ExpressionVisitor)
      acceptsNextSibling

    case InlineStmt(text) =>
      statements += StatementBuilder.inlineStmt(text)
      acceptsNextSibling

    case stmt =>
      println("Unhandled node: " + stmt)
      abort
  }
}
