/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.types

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.NamespaceName
import de.leanovate.jbj.runtime.context.Context
import scala.Some
import de.leanovate.jbj.runtime.adapter.PInterfaceMethod

trait PIterator extends DelegateObjectVal {
  def rewind()(implicit ctx: Context)

  def valid(implicit ctx: Context): Boolean

  def current(implicit ctx: Context): PAny

  def next()(implicit ctx: Context)

  def key(implicit ctx: Context): PVal

  override def foreachByVal[R](f: (PVal, PAny) => Option[R])(implicit ctx: Context): Option[R] = {
    rewind()
    var result = Option.empty[R]
    while (result.isEmpty && valid) {
      val currentValue = current
      val currentKey = new LazyVal {
        def value = key.concrete
      }

      result = f(currentKey, currentValue.asVar)

      if (result.isEmpty)
        next()
    }
    result
  }
}

object PIterator extends PInterface with PInterfaceAdapter[PIterator] {
  override def name = NamespaceName(relative = false, prefixed = false, "Iterator")

  override def interfaces = List(PTraversable)

  override def declaredConstants = Map.empty

  override def methods = Seq(
    PInterfaceMethod(this, "rewind"),
    PInterfaceMethod(this, "valid"),
    PInterfaceMethod(this, "current"),
    PInterfaceMethod(this, "next"),
    PInterfaceMethod(this, "key")
  ).map {
    method => method.name.toLowerCase -> method
  }.toMap

  def cast(_obj: ObjectVal): PIterator = new PIterator {
    def delegate = _obj

    def rewind()(implicit ctx: Context) {
      pClass.invokeMethod(Some(this), "rewind", Nil)
    }

    def valid(implicit ctx: Context) = {
      pClass.invokeMethod(Some(this), "valid", Nil).asVal.toBool.asBoolean
    }

    def current(implicit ctx: Context) =
      pClass.invokeMethod(Some(this), "current", Nil)

    def next()(implicit ctx: Context) {
      pClass.invokeMethod(Some(this), "next", Nil)
    }

    def key(implicit ctx: Context) =
      pClass.invokeMethod(Some(this), "key", Nil).asVal
  }
}