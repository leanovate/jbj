/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.value.ObjectPropertyKey.Key
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.PClass

class StdObjectVal(var pClass: PClass, var instanceNum: Long, protected[value] val keyValueMap: ExtendedLinkedHashMap[Key])
  extends ObjectVal {

  private var _refCount = 0

  override def refCount = _refCount

  override def retain() {
    _refCount += 1
  }

  override def release()(implicit ctx: Context) {
    _refCount -= 1
    if (_refCount == 0)
      pClass.destructInstance(this)
  }

  override def clone(implicit ctx: Context) = {
    val clone = new StdObjectVal(pClass, ctx.global.instanceCounter.incrementAndGet(), keyValueMap.clone())

    pClass.findMethod("__clone").foreach {
      method =>
        method.invoke(clone, Nil)
    }
    clone
  }

}
