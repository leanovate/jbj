/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime

import de.leanovate.jbj.core.ast.MemberModifier

case class PProperty(modifiers: Set[MemberModifier.Type], name: String, declaringClass: PClass) {
  lazy val isStatic = modifiers.contains(MemberModifier.STATIC)

  lazy val isPrivate = modifiers.contains(MemberModifier.PRIVATE)

  lazy val isProtected = modifiers.contains(MemberModifier.PROTECTED)

  lazy val isPublic = modifiers.contains(MemberModifier.PUBLIC)
}