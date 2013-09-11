/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.types

import de.leanovate.jbj.runtime.value.PAny
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.NamespaceName

trait PFunction {
  def name: NamespaceName

  def call(parameters: List[PParam])(implicit callerCtx: Context): PAny
}
