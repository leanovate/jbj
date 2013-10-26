/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.PParam

trait ParameterAdapter[T] {
  def requiredCount: Int

  def adapt(parameters: List[PParam], strict: Boolean, missingErrorHandler: => Unit, conversionErrorHandler: (String, String) => Unit)(implicit ctx: Context): (T, List[PParam])
}
