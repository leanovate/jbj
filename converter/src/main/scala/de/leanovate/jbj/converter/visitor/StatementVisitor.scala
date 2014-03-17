/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.converter.visitor

import de.leanovate.jbj.core.ast.{Expr, NodeVisitor}
import scala.text.Document
import scala.text.Document._
import de.leanovate.jbj.core.ast.stmt._
import de.leanovate.jbj.converter.builders.{CodeUnitBuilder, ProgCodeUnitBuilder, StatementBuilder}
import de.leanovate.jbj.core.ast.stmt.loop.ForStmt
import de.leanovate.jbj.core.ast.stmt.cond.IfStmt
import de.leanovate.jbj.core.ast.stmt.BlockStmt
import de.leanovate.jbj.core.ast.stmt.loop.ForStmt
import de.leanovate.jbj.core.ast.stmt.cond.IfStmt
import de.leanovate.jbj.core.ast.stmt.InlineStmt
import de.leanovate.jbj.core.ast.stmt.ExprStmt
import de.leanovate.jbj.core.ast.stmt.EchoStmt

class StatementVisitor(implicit builder: CodeUnitBuilder) extends NodeVisitor[Document] {
  val statements = Seq.newBuilder[Document]

  override def result = statements.result().reduceOption(_ :: _).getOrElse(empty)

  override def visit = {
    case EchoStmt(exprs) =>
      statements += "echo(" :: exprs.map(_.foldWith(new ExpressionVisitor)).reduceOption(_ :: ", " :: _).getOrElse(empty) :: ")" :: empty
      acceptsNextSibling

    case BlockStmt(body) =>
      statements += body.map(_.foldWith(new StatementVisitor)).reduceOption(_ :/: _).getOrElse(empty)
      acceptsNextSibling

    case ExprStmt(expr: Expr) =>
      statements += expr.foldWith(new ExpressionVisitor)
      acceptsNextSibling

    case ForStmt(before, condition, after, body) =>
      statements += "pFor(" :: inlineBlock(before) :: ", " :: inlineBlock(condition) :: ", " :: inlineBlock(after) :: ") {" ::
        nest(2, body.foldLeft(empty: Document) {
          (doc, stmt) => doc :/: stmt.foldWith(new StatementVisitor)
        }) :/: "}" :: empty
      acceptsNextSibling

    case IfStmt(condition, thenStmts, elseIfs, Nil) =>
      statements += "if(" :: condition.foldWith(new ExpressionVisitor) :: ") {" ::
        nest(2, thenStmts.foldLeft(empty: Document) {
          (doc, stmt) => doc :/: stmt.foldWith(new StatementVisitor)
        }) ::
        elseIfs.foldLeft(empty: Document) {
          (doc, elseIf) =>
            doc :/: "} else if(" :: elseIf.condition.foldWith(new ExpressionVisitor) :: ") {" ::
              nest(2, elseIf.themStmts.foldLeft(empty: Document) {
                (doc, stmt) => doc :/: stmt.foldWith(new StatementVisitor)
              })
        } :/: "}" :: empty
      acceptsNextSibling

    case IfStmt(condition, thenStmts, elseIfs, elseStmts) =>
      statements += "if(" :: condition.foldWith(new ExpressionVisitor) :: ") {" ::
        nest(2, thenStmts.foldLeft(empty: Document) {
          (doc, stmt) => doc :/: stmt.foldWith(new StatementVisitor)
        }) ::
        elseIfs.foldLeft(empty: Document) {
          (doc, elseIf) =>
            doc :/: "} else if(" :: elseIf.condition.foldWith(new ExpressionVisitor) :: ") {" ::
              nest(2, elseIf.themStmts.foldLeft(empty: Document) {
                (doc, stmt) => doc :/: stmt.foldWith(new StatementVisitor)
              })
        } :/: "} else { " ::
        nest(2, elseStmts.foldLeft(empty: Document) {
          (doc, stmt) => doc :/: stmt.foldWith(new StatementVisitor)
        }) :/: "}" :: empty
      acceptsNextSibling

    case InlineStmt(text) =>
      statements += StatementBuilder.inlineStmt(text)
      acceptsNextSibling

    case ReturnStmt(None) =>
      statements += text("return")
      acceptsNextSibling

    case ReturnStmt(Some(expr)) =>
      statements += "return " :: expr.foldWith(new ExpressionVisitor)
      acceptsNextSibling


    case stmt =>
      println("Unhandled node: " + stmt)
      abort
  }

  private def inlineBlock(exprs: List[Expr]) = {
    if (exprs.size == 1)
      exprs(0).foldWith(new ExpressionVisitor)
    else
      "{" :: exprs.map(_.foldWith(new ExpressionVisitor)).reduceOption(_ :: "; " :: _).getOrElse(empty) :: "}" :: empty
  }
}
