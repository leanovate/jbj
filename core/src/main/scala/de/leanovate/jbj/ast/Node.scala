package de.leanovate.jbj.ast

import java.io.PrintStream

trait Node {
  private var _position: NodePosition = NoNodePosition

  implicit def position = _position

  def position_=(pos: NodePosition) {
    if (_position == NoNodePosition)
      _position = pos
  }

  def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName + " " + position)
  }
}
