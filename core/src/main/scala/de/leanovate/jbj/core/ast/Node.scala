package de.leanovate.jbj.core.ast

import java.io.PrintStream
import scala.xml.Elem

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

  def toXml: Elem = <node/>.copy(label = getClass.getSimpleName)

  def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName)
  }
}
