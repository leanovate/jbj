/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime


trait PInterface {
  def name: NamespaceName

  def interfaces: List[PInterface]

  def methods: Map[String, PMethod]

  final def isAssignableFrom(other: PClass): Boolean = other.interfaces.contains(this)
}
