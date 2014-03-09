package de.leanovate.jbj.converter.visitor

import de.leanovate.jbj.core.ast.{Stmt, Prog, NodeVisitor}
import scala.text.Document
import de.leanovate.jbj.converter.builders.CodeUnitBuilder

class ProgVisitor(name: String, packageName: Option[String]) extends NodeVisitor[Document] {
  val builder = new CodeUnitBuilder(name, packageName)

  def result = builder.build()

  def visit = {
    case prop: Prog =>
      acceptsNextChild
    case stmt: Stmt =>
      builder.addStatement(stmt.foldWith(new StatementVisitor))
      acceptsNextSibling
  }
}
