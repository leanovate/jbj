/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.types.{PParamDef, PParam, PClass, PMethod}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

abstract class InstanceMethod(val declaringClass: PClass, val name: String,
                              val parameters: Seq[PParamDef] = Seq.empty,
                              val isFinal: Boolean = false) extends PMethod {
  def isAbstract = false

  def isStatic = false

  def isPrivate = false

  def isProtected = false

  def invokeStatic(ctx: Context, parameters: List[PParam]) = {
    throw new FatalErrorJbjException("Non-static method %s::%s() cannot be called statically".format(declaringClass.name.toString, name))(ctx)
  }
}
