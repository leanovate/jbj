package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast.{NodeVisitor, Node, Stmt, Expr}

case class ElseIfBlock(condition: Expr, themStmts: List[Stmt]) extends Node {
  override def visit[R](visitor: NodeVisitor[R])= visitor(this).thenChild(condition).thenChildren(themStmts)

}