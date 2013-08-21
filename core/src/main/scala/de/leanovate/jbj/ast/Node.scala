package de.leanovate.jbj.ast

import java.io.PrintStream
import scala.xml.{Elem, NodeSeq}

trait Node {
  def toXml : Elem = <node/>.copy(label = getClass.getSimpleName)

  def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName)
  }
}
