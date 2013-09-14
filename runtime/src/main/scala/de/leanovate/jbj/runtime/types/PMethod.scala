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

  def parameters: Seq[PParamDef]

  def isFinal: Boolean

  def isAbstract: Boolean

  def isStatic: Boolean

  def isPrivate: Boolean

  def isProtected: Boolean

  def invoke(instance: ObjectVal, parameters: List[PParam])(implicit callerCtx: Context): PAny

  def invokeStatic(parameters: List[PParam])(implicit callerCtx: Context): PAny

  def isCompatibleWith(otherMethod: PMethod): Boolean = {
    val otherParameters = otherMethod.parameters
    parameters.zipWithIndex.forall {
      case (parameter, idx) if idx < otherParameters.size =>
        parameter.isCompatible(otherParameters(idx))
      case (parameter, _) =>
        parameter.hasDefault
    }
  }
}
