/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.buildins.types

import de.leanovate.jbj.runtime.types.{PException, PInterfaceMethod, PInterface}
import de.leanovate.jbj.runtime.NamespaceName
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.value.{PAny, ObjectVal}
import de.leanovate.jbj.runtime.exception.RuntimeJbjException

trait PIteratorAggregate {
  def obj: ObjectVal

  def getIterator()(implicit ctx: Context): PIterator
}

object PIteratorAggregate extends PInterface {
  override def name = NamespaceName(relative = false, "IteratorAggregate")

  override def interfaces = List(PTraversable)

  override def declaredConstants = Map.empty

  override def methods = Seq(
    PInterfaceMethod(this, "getIterator")
  ).map {
    method => method.name.toLowerCase -> method
  }.toMap

  def cast(_obj: ObjectVal): PIteratorAggregate = new PIteratorAggregate {
    def obj = _obj

    def getIterator()(implicit ctx: Context) = {
      _obj.pClass.invokeMethod(Some(obj), "getIterator", Nil).asVal.concrete match {
        case obj: ObjectVal if obj.instanceOf(PIterator) =>
          PIterator.cast(obj)
        case _ =>
          throw RuntimeJbjException("Objects returned by %s::getIterator() must be traversable or implement interface Iterator".
            format(obj.pClass.name.toString))
      }
    }
  }
}