package de.leanovate.jbj.core.ast


trait HasNodePosition {
  self: Node =>

  private var _position: NodePosition = NoNodePosition

  implicit def position = _position

  def position_=(pos: NodePosition) {
    if (_position == NoNodePosition)
      _position = pos
  }
}
