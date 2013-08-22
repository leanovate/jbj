package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{NodeVisitor, Expr}

trait BinaryExpr extends Expr {
  def left: Expr

  def right: Expr

  override def toXml =
    <binary>
      <left>
        {left.toXml}
      </left>
      <right>
        {right.toXml}
      </right>
    </binary>.copy(label = getClass.getSimpleName)

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChild(left).thenChild(right)
}
