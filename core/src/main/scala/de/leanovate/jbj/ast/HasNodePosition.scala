package de.leanovate.jbj.ast

import java.io.PrintStream
import scala.xml.Elem

trait HasNodePosition {
  self: Node =>

  private var _position: NodePosition = NoNodePosition

  implicit def position = _position

  def position_=(pos: NodePosition) {
    if (_position == NoNodePosition)
      _position = pos
  }

  override def toXml: Elem = <node line={position.line.toString} file={position.fileName}/>.copy(label = getClass.getSimpleName)

  override def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName + " " + position)
  }
}
