/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.context

sealed trait ConstantKey

case class CaseSensitiveConstantKey(name: Seq[String]) extends ConstantKey {
  override def toString = name.mkString("\\")
}

case class CaseInsensitiveConstantKey(name: Seq[String]) extends ConstantKey {
  override def toString = name.mkString("\\")
}