/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.types

import de.leanovate.jbj.runtime.NamespaceName
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.value.{ObjectVal, PVal}

trait PInterface {
  def name: NamespaceName

  def interfaces: List[PInterface]

  def declaredConstants: Map[String, PVal]

  def interfaceConstants: Map[String, PVal] =
    interfaces.flatMap(_.interfaceConstants.toList).toMap ++ declaredConstants.toMap

  def methods: Map[String, PMethod]

  def initializeClass(pClass: PClass)(implicit ctx: Context) {}

  final def isAssignableFrom(other: PClass): Boolean = other.interfaces.contains(this)
}

trait PInterfaceAdapter[T] {
  self: PInterface =>

  def name: NamespaceName

  def cast(obj: ObjectVal): T
}