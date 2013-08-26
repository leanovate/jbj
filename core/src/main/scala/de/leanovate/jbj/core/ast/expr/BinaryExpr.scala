package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{NodeVisitor, Expr}

trait BinaryExpr extends Expr {
  def left: Expr

  def right: Expr

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChild(left).thenChild(right)
}
