package de.leanovate.jbj.core.ast

import java.io.PrintStream
import scala.xml.Elem

trait Node {
  var deprecated: Option[String] = None

  def toXml: Elem = <node/>.copy(label = getClass.getSimpleName)

  def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName)
  }

  def visit[R](visitor: NodeVisitor[R]): NodeVisitor.Result[R] = visitor(this)
}
