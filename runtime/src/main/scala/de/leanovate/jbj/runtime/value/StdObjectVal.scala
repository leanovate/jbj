/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.value

import scala.collection.mutable
import de.leanovate.jbj.runtime.value.ObjectPropertyKey.Key
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.PClass

class StdObjectVal(var pClass: PClass, var instanceNum: Long, protected val keyValueMap: mutable.LinkedHashMap[Key, PAny])
  extends ObjectVal {

  override def clone(implicit ctx: Context) = {
    val clone = new StdObjectVal(pClass, ctx.global.instanceCounter.incrementAndGet(), keyValueMap.clone())

    pClass.findMethod("__clone").foreach {
      method =>
        method.invoke(ctx, clone, Nil)
    }
    clone
  }

}
