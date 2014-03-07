/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast

/**
 * Generic node within the AST.
 */
trait Node {
  /**
   * Any deprecation warnings associated with this node.
   */
  var deprecated: Option[String] = None

  /**
   * Visit the node with a [[NodeVisitor]].
   */
  def accept[R](visitor: NodeVisitor[R]): NodeVisitor.Action[R] = visitor(this)

  def foldWith[R](visitor: NodeVisitor[R]) = accept(visitor).result
}
