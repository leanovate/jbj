package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{NodeVisitor, NamespaceName, Stmt, Node}

case class CatchBlock(exceptionName: NamespaceName, variableName: String, stmts: List[Stmt]) extends Node {
  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(stmts)
}
