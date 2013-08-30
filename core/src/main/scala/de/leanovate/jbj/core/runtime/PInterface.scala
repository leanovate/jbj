/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.runtime

import de.leanovate.jbj.core.ast.NamespaceName
import scala.annotation.tailrec

trait PInterface {
  def name: NamespaceName

  def interfaces: Seq[PInterface]

  def methods: Map[String, PMethod]

  @tailrec
  final def isAssignableFrom(other: PClass): Boolean = other.interfaces.contains(this) || (other.superClass match {
    case None => false
    case Some(s) => isAssignableFrom(s)
  })
}
