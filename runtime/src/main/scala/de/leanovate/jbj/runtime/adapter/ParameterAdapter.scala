/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.PParam
import de.leanovate.jbj.runtime.value.PVal
import de.leanovate.jbj.runtime.exception.WarnWithResultJbjException

trait ParameterAdapter[T] {
  def parameterIdx: Int

  def requiredCount: Int

  def adapt(parameters: List[PParam], strict: Boolean,
            missingErrorHandler: () => Unit,
            conversionErrorHandler: (String, String, Int) => Unit)(implicit ctx: Context): (T, List[PParam])
}

object ParameterAdapter {
  val conversionErrorIgnore = {
    (expectedTypeName: String, givenTypeName: String, parameterIdx: Int) =>
  }

  def conversionErrorWarn(name: String, result: PVal)(implicit ctx: String) = {
    (expectedTypeName: String, givenTypeName: String, parameterIdx: Int) =>
      val msg = s"$name expects parameter $parameterIdx to be $expectedTypeName, $givenTypeName given"
      throw new WarnWithResultJbjException(msg, result)
  }

}