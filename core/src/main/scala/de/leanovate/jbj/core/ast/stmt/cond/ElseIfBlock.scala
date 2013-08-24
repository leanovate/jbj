package de.leanovate.jbj.core.ast.stmt.cond

import de.leanovate.jbj.core.ast.{NodeVisitor, Node, Stmt, Expr}

case class ElseIfBlock(condition: Expr, themStmts: List[Stmt]) extends Node {
  override def visit[R](visitor: NodeVisitor[R])= visitor(this).thenChild(condition).thenChildren(themStmts)

}