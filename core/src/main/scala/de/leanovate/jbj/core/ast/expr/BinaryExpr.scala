/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{NodeVisitor, Expr}

trait BinaryExpr extends Expr {
  def left: Expr

  def right: Expr

  def precedence: Precedence.Type

  override def accept[R](visitor: NodeVisitor[R]) = visitor(this).thenChild(left).thenChild(right)
}
