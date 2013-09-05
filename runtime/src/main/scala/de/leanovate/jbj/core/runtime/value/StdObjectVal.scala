/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.runtime.value

import de.leanovate.jbj.core.runtime.PClass
import scala.collection.mutable
import de.leanovate.jbj.core.runtime.value.ObjectPropertyKey.Key
import de.leanovate.jbj.core.runtime.context.Context

class StdObjectVal(var pClass: PClass, var instanceNum: Long, protected val keyValueMap: mutable.LinkedHashMap[Key, PAny])
  extends ObjectVal {

  override def clone(implicit ctx: Context) =
    new StdObjectVal(pClass, ctx.global.instanceCounter.incrementAndGet(), keyValueMap.clone())

}
