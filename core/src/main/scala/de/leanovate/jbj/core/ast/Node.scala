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
  def visit[R](visitor: NodeVisitor[R]): NodeVisitor.Result[R] = visitor(this)
}
