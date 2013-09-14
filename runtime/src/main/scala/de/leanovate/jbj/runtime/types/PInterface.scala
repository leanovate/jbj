/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.types

import de.leanovate.jbj.runtime.NamespaceName
import de.leanovate.jbj.runtime.value.ConstVal

trait PInterface {
  def name: NamespaceName

  def interfaces: List[PInterface]

  def interfaceConstants: Map[String, ConstVal]

  def methods: Map[String, PMethod]

  final def isAssignableFrom(other: PClass): Boolean = other.interfaces.contains(this)
}
