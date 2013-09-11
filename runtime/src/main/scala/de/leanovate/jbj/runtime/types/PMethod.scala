/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.types

import de.leanovate.jbj.runtime.value.{PAny, ObjectVal}
import de.leanovate.jbj.runtime.context.Context

trait PMethod {
  def declaringClass: PClass

  def name: String

  def isFinal: Boolean

  def isAbstract: Boolean

  def isStatic: Boolean

  def isPrivate: Boolean

  def isProtected: Boolean

  def invoke(ctx: Context, instance: ObjectVal, parameters: List[PParam]): PAny

  def invokeStatic(ctx: Context, parameters: List[PParam]): PAny
}
