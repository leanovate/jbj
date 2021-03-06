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
  def declaringInterface: Option[PInterface]

  def implementingClass: PClass

  def name: String

  def returnByRef: Boolean

  def parameters: Seq[PParamDef]

  def isFinal: Boolean

  def isAbstract: Boolean

  def isStatic: Boolean

  def isPrivate: Boolean

  def isProtected: Boolean

  def isPublic = !isProtected && !isPrivate

  def invoke(instance: ObjectVal, parameters: List[PParam])(implicit callerCtx: Context): PAny

  def invokeStatic(parameters: List[PParam], strict: Boolean = true)(implicit callerCtx: Context): PAny

  def isCompatibleWith(otherMethod: PMethod): Boolean = {
    if (returnByRef != otherMethod.returnByRef)
      false
    else {
      val otherParameters = otherMethod.parameters
      parameters.zipWithIndex.forall {
        case (parameter, idx) if idx < otherParameters.size =>
          parameter.isCompatible(otherParameters(idx))
        case (parameter, _) =>
          parameter.default.isDefined
      }
    }
  }
}
