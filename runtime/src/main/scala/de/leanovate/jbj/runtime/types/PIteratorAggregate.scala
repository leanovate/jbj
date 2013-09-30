/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.types

import de.leanovate.jbj.runtime.NamespaceName
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.exception.RuntimeJbjException
import scala.Some
import de.leanovate.jbj.runtime.adapter.PInterfaceMethod

trait PIteratorAggregate extends DelegateObjectVal {
  def getIterator()(implicit ctx: Context): PIterator

  override def foreachByVal[R](f: (PVal, PAny) => Option[R])(implicit ctx: Context): Option[R] = {
    val iterator = getIterator()
    iterator.retain()

    try {
      iterator.foreachByVal(f)
    } finally {
      iterator.release()
    }
  }
}

object PIteratorAggregate extends PInterface with PInterfaceAdapter[PIteratorAggregate] {
  override def name = NamespaceName(relative = false, prefixed = false, "IteratorAggregate")

  override def interfaces = List(PTraversable)

  override def declaredConstants = Map.empty

  override def methods = Seq(
    PInterfaceMethod(this, "getIterator")
  ).map {
    method => method.name.toLowerCase -> method
  }.toMap

  def cast(_obj: ObjectVal): PIteratorAggregate = new PIteratorAggregate {
    def delegate = _obj

    def getIterator()(implicit ctx: Context) = {
      _obj.pClass.invokeMethod(Some(this), "getIterator", Nil).asVal.concrete match {
        case obj: ObjectVal if obj.instanceOf(PIterator) =>
          PIterator.cast(obj)
        case obj: ObjectVal if obj.instanceOf(PIteratorAggregate) =>
          PIteratorAggregate.cast(obj).getIterator()
        case _ =>
          throw RuntimeJbjException("Objects returned by %s::getIterator() must be traversable or implement interface Iterator".
            format(pClass.name.toString))
      }
    }
  }
}