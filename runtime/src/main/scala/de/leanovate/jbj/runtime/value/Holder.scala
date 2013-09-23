/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.value

class Holder[A <: AnyRef](default: => A) {
  private var current: A = null.asInstanceOf[A]

  @inline
  def isEmpty: Boolean = current eq null

  @inline
  def isDefined: Boolean = current ne null

  @inline
  def get() = {
    if (isEmpty)
      current = default
    current
  }

  @inline
  def map[B](f: A => B): Option[B] = if (isEmpty) None else Some(f(current))

  @inline
  def clear() {
    current = null.asInstanceOf[A]
  }

  @inline
  def set(_current: A) {
    current = _current
  }
}
