/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.context

import de.leanovate.jbj.runtime.value.PAny

trait FunctionLikeContext extends Context {

  var functionArguments: Seq[PAny] = Seq.empty

  def functionSignature: String

  def callerContext: Context
}
