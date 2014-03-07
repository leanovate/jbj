package de.leanovate.jbj.transcode.visitor

import de.leanovate.jbj.core.ast.{Stmt, Prog, NodeVisitor}
import scala.text.Document
import de.leanovate.jbj.transcode.builders.CodeUnitBuilder

class ProgVisitor(name: String) extends NodeVisitor[Document] {
  val builder = new CodeUnitBuilder(name)

  def result = builder.build()

  def visit = {
    case prop: Prog =>
      acceptsNextChild
    case stmt: Stmt =>
      builder.addStatement(stmt.foldWith(new StatementVisitor))
      acceptsNextSibling
  }
}
