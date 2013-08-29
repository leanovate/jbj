/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

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
