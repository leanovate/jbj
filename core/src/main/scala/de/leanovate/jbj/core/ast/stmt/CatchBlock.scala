package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.{NodeVisitor, NamespaceName, Stmt, Node}

case class CatchBlock(exceptionName: NamespaceName, variableName: String, stmts: List[Stmt]) extends Node {
  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(stmts)
}
