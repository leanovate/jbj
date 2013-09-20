/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.exception

import de.leanovate.jbj.runtime.value.{StringVal, ObjectVal}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.api.http.JbjException
import de.leanovate.jbj.runtime.types.{PAnyParam, PException}

case class RuntimeJbjException(exception: ObjectVal)(implicit ctx: Context)
  extends JbjException(exception.getProperty("message", None).map(_.asVal.toStr.asString).getOrElse("")) {
}

object RuntimeJbjException {
  def apply(msg: String)(implicit ctx: Context) = {
    val exception = PException.newInstance(PAnyParam(StringVal(msg)) :: Nil)
    new RuntimeJbjException(exception)
  }
}