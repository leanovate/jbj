/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.exception

import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.api.http.JbjException

class CompileErrorException(message: String)(implicit ctx: Context) extends JbjException(message) {

  ctx.log.compileError(message)
}