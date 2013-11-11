/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast

import de.leanovate.jbj.runtime.context.Context
import scala.collection.mutable
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

object MemberModifier extends Enumeration {
  type Type = Value
  val PUBLIC, PROTECTED, PRIVATE, STATIC, FINAL, ABSTRACT = Value

  def checkMultiple(modifiers: Seq[MemberModifier.Type])(implicit ctx: Context) {
    val modifierSet = mutable.Set.empty[MemberModifier.Type]
    modifiers.foreach {
      case ABSTRACT if modifierSet.contains(ABSTRACT) =>
        throw new FatalErrorJbjException("Multiple abstract modifiers are not allowed")
      case modifier if modifierSet.contains(modifier) =>
        throw new FatalErrorJbjException("Multiple access type modifiers are not allowed")
      case modifier =>
        modifierSet.add(modifier)
    }
  }
}
